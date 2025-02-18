/**********************************************************************
 * Copyright (c) by Heiner Jostkleigrewe
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 *  This program is distributed in the hope that it will be useful,  but WITHOUT ANY WARRANTY; without
 *  even the implied warranty of  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 *  the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this program.  If not,
 * see <http://www.gnu.org/licenses/>.
 * <p>
 * heiner@jverein.de
 * www.jverein.de
 **********************************************************************/
package de.jost_net.JVerein.gui.control;

import de.jost_net.JVerein.DBTools.DBTransaction;
import de.jost_net.JVerein.Einstellungen;
import de.jost_net.JVerein.gui.action.EditAction;
import de.jost_net.JVerein.gui.action.WirtschaftsplanPostenDialogAction;
import de.jost_net.JVerein.gui.menu.WirtschaftsplanungListMenu;
import de.jost_net.JVerein.gui.parts.WirtschaftsplanUebersichtPart;
import de.jost_net.JVerein.gui.view.WirtschaftsplanungView;
import de.jost_net.JVerein.io.WirtschaftsplanungCSV;
import de.jost_net.JVerein.io.WirtschaftsplanungPDF;
import de.jost_net.JVerein.rmi.Buchungsklasse;
import de.jost_net.JVerein.rmi.Wirtschaftsplan;
import de.jost_net.JVerein.rmi.WirtschaftsplanItem;
import de.jost_net.JVerein.util.Dateiname;
import de.jost_net.JVerein.util.JVDateFormatTTMMJJJJ;
import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.parts.TreePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.jameica.system.Settings;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WirtschaftsplanungControl extends AbstractControl
{
  private TreePart einnahmen;

  private TreePart ausgaben;

  private WirtschaftsplanUebersichtPart uebersicht;

  public final static String AUSWERTUNG_PDF = "PDF";

  public final static String AUSWERTUNG_CSV = "CSV";

  private final static int EINNAHME = 0;
  private final static int AUSGABE = 1;

  private final static int ID_COL = 1;
  private final static int BETRAG_COL = 2;


  /**
   * Erzeugt einen neuen AbstractControl der fuer die angegebene View.
   *
   * @param view
   *     die View, fuer die dieser WirtschaftsplanungControl zustaendig ist.
   */
  public WirtschaftsplanungControl(AbstractView view)
  {
    super(view);
    de.willuhn.jameica.system.Settings settings = new de.willuhn.jameica.system.Settings(
        this.getClass());
    settings.setStoreWhenRead(true);
  }

  public Part getWirtschaftsplanungList() throws RemoteException
  {
    DBService service = Einstellungen.getDBService();
    List<Wirtschaftsplan> plaene = new ArrayList<>();

    GenericIterator<Wirtschaftsplan> iterator = service.createList(Wirtschaftsplan.class);
    while (iterator.hasNext())
    {
      plaene.add(iterator.next());
    }

    TablePart wirtschaftsplaene = new TablePart(
        plaene, new EditAction(WirtschaftsplanungView.class));

    CurrencyFormatter formatter = new CurrencyFormatter("",
        Einstellungen.DECIMALFORMAT);
    DateFormatter dateFormatter = new DateFormatter(new JVDateFormatTTMMJJJJ());

    wirtschaftsplaene.addColumn("ID", "id");
    wirtschaftsplaene.addColumn("Von", "datum_von", dateFormatter);
    wirtschaftsplaene.addColumn("Bis", "datum_bis", dateFormatter);
    wirtschaftsplaene.addColumn("Einnahmen Soll", "planEinnahme", formatter);
    wirtschaftsplaene.addColumn("Ausgaben Soll", "planAusgabe", formatter);
    wirtschaftsplaene.addColumn("Saldo Soll", "planSaldo", formatter);
    wirtschaftsplaene.addColumn("Einnahmen Ist", "istEinnahme", formatter);
    wirtschaftsplaene.addColumn("Ausgaben Ist", "istAusgabe", formatter);
    wirtschaftsplaene.addColumn("Saldo Ist", "istSaldo", formatter);
    wirtschaftsplaene.addColumn("Saldo Differenz", "differenz", formatter);

    wirtschaftsplaene.setContextMenu(new WirtschaftsplanungListMenu());

    return wirtschaftsplaene;
  }

  public Wirtschaftsplan getWirtschaftsplan()
  {
    if (getCurrentObject() instanceof Wirtschaftsplan)
    {
      return (Wirtschaftsplan) getCurrentObject();
    }
    return null;
  }

  public TreePart getEinnahmen() throws RemoteException
  {
    if (einnahmen == null)
    {
      einnahmen = generateTree(EINNAHME);
    }
    else
    {
      @SuppressWarnings("rawtypes") List items = einnahmen.getItems();
      einnahmen.removeAll();
      einnahmen.setList(items);
    }
    return einnahmen;
  }

  public TreePart getAusgaben() throws RemoteException
  {
    if (ausgaben == null)
    {
      ausgaben = generateTree(AUSGABE);
    }
    else
    {
      @SuppressWarnings("rawtypes") List items = ausgaben.getItems();
      ausgaben.removeAll();
      ausgaben.setList(items);
    }
    return ausgaben;
  }

  private TreePart generateTree(int art) throws RemoteException
  {
    Wirtschaftsplan wirtschaftsplan = getWirtschaftsplan();

    if (wirtschaftsplan == null)
    {
      return null;
    }

    Map<String, WirtschaftsplanungNode> nodes = new HashMap<>();

    DBService service = Einstellungen.getDBService();

    DBIterator<Buchungsklasse> buchungsklasseIterator = service.createList(Buchungsklasse.class);
    while (buchungsklasseIterator.hasNext())
    {
      Buchungsklasse klasse = buchungsklasseIterator.next();
      nodes.put(klasse.getID(), new WirtschaftsplanungNode(klasse, art, wirtschaftsplan));
    }

    String sql = "SELECT wirtschaftsplanitem.buchungsklasse, sum(soll) " +
        "FROM wirtschaftsplanitem, buchungsart " +
        "WHERE wirtschaftsplan = ? AND wirtschaftsplanitem.buchungsart = buchungsart.id AND buchungsart.art = ? " +
        "GROUP BY wirtschaftsplanitem.buchungsklasse";

    service.execute(sql, new Object[] { wirtschaftsplan.getID(), art }, resultSet -> {
      while (resultSet.next())
      {
        DBIterator<Buchungsklasse> iterator = service.createList(
            Buchungsklasse.class);
        iterator.addFilter("id = ?", resultSet.getLong(ID_COL));
        if (!iterator.hasNext())
        {
          continue;
        }

        double soll = resultSet.getDouble(BETRAG_COL);
        nodes.get(resultSet.getString(ID_COL)).setSoll(soll);
      }

      return nodes;
    });

    if (Einstellungen.getEinstellung().getBuchungsklasseInBuchung())
    {
      sql = "SELECT buchung.buchungsklasse, sum(buchung.betrag) " +
          "FROM buchung, buchungsart " +
          "WHERE buchung.buchungsart = buchungsart.id " +
          "AND buchung.datum >= ? AND buchung.datum <= ? " +
          "AND buchungsart.art = ? " +
          "GROUP BY buchung.buchungsklasse";

    }
    else
    {
      sql = "SELECT buchungsart.buchungsklasse, sum(buchung.betrag) " +
          "FROM buchung, buchungsart " +
          "WHERE buchung.buchungsart = buchungsart.id " +
          "AND buchung.datum >= ? AND buchung.datum <= ? " +
          "AND buchungsart.art = ? " +
          "GROUP BY buchungsart.buchungsklasse";

    }
    service.execute(sql,
        new Object[] { wirtschaftsplan.getDatumVon(),
            wirtschaftsplan.getDatumBis(), art }, resultSet -> {
          while (resultSet.next())
          {
            DBIterator<Buchungsklasse> iterator = service.createList(
                Buchungsklasse.class);
            String key = resultSet.getString(ID_COL);
            iterator.addFilter("id = ?", key);
            if (!iterator.hasNext())
            {
              continue;
            }

            double ist = resultSet.getDouble(BETRAG_COL);
            nodes.get(key).setIst(ist);
          }

          return nodes;
        });

    TreePart treePart = new TreePart(new ArrayList<>(nodes.values()), new WirtschaftsplanPostenDialogAction(this, art));

    CurrencyFormatter formatter = new CurrencyFormatter("",
        Einstellungen.DECIMALFORMAT);
    treePart.addColumn("Buchungsklasse", "buchungsklassebezeichnung");
    treePart.addColumn("Buchungsart / Posten", "buchungsartbezeichnung_posten");
    treePart.addColumn("Soll", "soll", formatter);
    treePart.addColumn("Ist", "ist", formatter);

    return treePart;
  }

  public void setUebersicht(WirtschaftsplanUebersichtPart uebersicht)
  {
    this.uebersicht = uebersicht;
  }

  public void reloadSoll(WirtschaftsplanungNode parent, int art)
      throws RemoteException, ApplicationException
  {
    while (parent != null)
    {
      @SuppressWarnings("rawtypes") GenericIterator iterator = parent.getChildren();
      double soll = 0;
      while (iterator.hasNext())
      {
        WirtschaftsplanungNode child = (WirtschaftsplanungNode) iterator.next();
        soll += child.getSoll();
      }
      parent.setSoll(soll);

      parent = (WirtschaftsplanungNode) parent.getParent();
    }

    if (art == 0)
    {
      getEinnahmen();
    }
    else
    {
      getAusgaben();
    }

    uebersicht.updateSoll();
  }

  public void handleStore()
  {
    try
    {
      @SuppressWarnings("unchecked") List<WirtschaftsplanungNode> rootNodesEinnahmen = (List<WirtschaftsplanungNode>) einnahmen.getItems();
      @SuppressWarnings("unchecked") List<WirtschaftsplanungNode> rootNodesAusgaben = (List<WirtschaftsplanungNode>) ausgaben.getItems();

      DBService service = Einstellungen.getDBService();
      Wirtschaftsplan wirtschaftsplan = getWirtschaftsplan();

      DBTransaction.starten();

      checkDate();

      Date von = (Date) uebersicht.getVon().getValue();
      Date bis = (Date) uebersicht.getBis().getValue();
      wirtschaftsplan.setDatumBis(bis);
      wirtschaftsplan.setDatumVon(von);
      wirtschaftsplan.store();

      if (!wirtschaftsplan.isNewObject())
      {
        DBIterator<WirtschaftsplanItem> iterator = service.createList(
            WirtschaftsplanItem.class);
        iterator.addFilter("wirtschaftsplan = ?", wirtschaftsplan.getID());
        while (iterator.hasNext())
        {
          iterator.next().delete(); //L�schen alter Eintr�ge, wird sp�ter neu angelegt
        }
      }

      for (WirtschaftsplanungNode rootNode : rootNodesEinnahmen)
      {
        storeNodes(rootNode.getChildren(), wirtschaftsplan.getID());
      }
      for (WirtschaftsplanungNode rootNode : rootNodesAusgaben)
      {
        storeNodes(rootNode.getChildren(), wirtschaftsplan.getID());
      }

      DBTransaction.commit();

      view.reload();

      GUI.getStatusBar().setSuccessText("Wirtschaftsplan gespeichert");
    }
    catch (ApplicationException e)
    {
      DBTransaction.rollback();

      GUI.getStatusBar().setErrorText(e.getMessage());
    }
    catch (RemoteException e)
    {
      DBTransaction.rollback();

      String fehler = "Fehler beim Speichern des Wirtschaftsplans";
      Logger.error(fehler, e);
      GUI.getStatusBar().setErrorText(fehler);
    }
  }

  @SuppressWarnings("rawtypes")
  private void storeNodes(GenericIterator iterator, String id)
      throws RemoteException, ApplicationException
  {
    while (iterator.hasNext())
    {
      WirtschaftsplanungNode currentNode = (WirtschaftsplanungNode) iterator.next();
      if (currentNode.getType().equals(WirtschaftsplanungNode.Type.POSTEN))
      {
        WirtschaftsplanItem item = Einstellungen.getDBService()
            .createObject(WirtschaftsplanItem.class, null);
        WirtschaftsplanItem oldItem = currentNode.getWirtschaftsplanItem();
        item.setPosten(oldItem.getPosten());
        item.setSoll(oldItem.getSoll());
        item.setWirtschaftsplanId(id);
        WirtschaftsplanungNode parent = (WirtschaftsplanungNode) currentNode.getParent();
        item.setBuchungsartId(parent.getBuchungsart().getID());
        WirtschaftsplanungNode root = (WirtschaftsplanungNode) parent.getParent();
        item.setBuchungsklasseId(root.getBuchungsklasse().getID());
        item.store();
      }
      else
      {
        storeNodes(currentNode.getChildren(), id);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void starteAuswertung(String type) throws ApplicationException
  {
    handleStore();

    FileDialog fd = new FileDialog(GUI.getShell(), SWT.SAVE);
    fd.setText("Ausgabedatei w�hlen.");
    //
    Settings settings = new Settings(this.getClass());
    //
    String path = settings.getString("lastdir",
        System.getProperty("user.home"));
    if (path != null && path.length() > 0)
    {
      fd.setFilterPath(path);
    }


    try
    {
      fd.setFileName(new Dateiname("wirtschaftsplan", "",
          Einstellungen.getEinstellung().getDateinamenmuster(), type).get());
    }
    catch (RemoteException e)
    {
      throw new ApplicationException(
          String.format("Fehler beim Erstellen der Datei: %s", e.getMessage()));
    }

    final String s = fd.open();

    if (s == null || s.length() == 0)
    {
      return;
    }

    final File file = new File(s);
    settings.setAttribute("lastdir", file.getParent());

    List<WirtschaftsplanungNode> einnahmenList;
    List<WirtschaftsplanungNode> ausgabenList;

    try
    {
      einnahmenList = (List<WirtschaftsplanungNode>) einnahmen.getItems();
      ausgabenList = (List<WirtschaftsplanungNode>) ausgaben.getItems();
    }
    catch (RemoteException e)
    {
      throw new ApplicationException(
          String.format("Fehler beim Erstellen der Reports: %s",
              e.getMessage()));
    }

    BackgroundTask task = new BackgroundTask()
    {
      @Override
      public void run(ProgressMonitor monitor) throws ApplicationException
      {
        switch (type)
        {
          case AUSWERTUNG_CSV:
            new WirtschaftsplanungCSV(einnahmenList, ausgabenList, file);
            break;
          case AUSWERTUNG_PDF:
            new WirtschaftsplanungPDF(einnahmenList, ausgabenList, file,
                getWirtschaftsplan());
            break;
          default:
            GUI.getStatusBar()
                .setErrorText("Unable to create Report. Unknown format!");
            return;
        }
        GUI.getCurrentView().reload();
      }

      @Override
      public void interrupt()
      {

      }

      @Override
      public boolean isInterrupted()
      {
        return false;
      }
    };
    Application.getController().start(task);
  }

  public void checkDate() throws ApplicationException
  {
    Date von = (Date) uebersicht.getVon().getValue();
    Date bis = (Date) uebersicht.getBis().getValue();
    if (von == null)
    {
      throw new ApplicationException("Von-Datum darf nicht leer sein!");
    }
    if (bis == null)
    {
      throw new ApplicationException("Bis-Datum darf nicht leer sein!");
    }
    if (bis.before(von))
    {
      throw new ApplicationException("Bis-Datum muss nach Von-Datum liegen");
    }
  }
}
