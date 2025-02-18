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

import de.jost_net.JVerein.Einstellungen;
import de.jost_net.JVerein.rmi.Buchungsart;
import de.jost_net.JVerein.rmi.Buchungsklasse;
import de.jost_net.JVerein.rmi.Wirtschaftsplan;
import de.jost_net.JVerein.rmi.WirtschaftsplanItem;
import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.GenericObject;
import de.willuhn.datasource.GenericObjectNode;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WirtschaftsplanungNode implements GenericObjectNode
{

  public enum Type
  {BUCHUNGSKLASSE, BUCHUNGSART, POSTEN, UNBEKANNT}

  Type type;

  private Buchungsklasse buchungsklasse;

  private Buchungsart buchungsart;

  private WirtschaftsplanItem wirtschaftsplanItem;

  private double soll;

  private double ist = 0;

  private WirtschaftsplanungNode parent;

  @SuppressWarnings("FieldMayBeFinal")
  private List<WirtschaftsplanungNode> children;

  private final static int ID_COL = 1;

  private final static int BETRAG_COL = 2;

  public WirtschaftsplanungNode(Buchungsklasse buchungsklasse, int art,
      Wirtschaftsplan wirtschaftsplan) throws RemoteException
  {
    type = Type.BUCHUNGSKLASSE;
    this.soll = 0;
    ist = 0;
    this.buchungsklasse = buchungsklasse;

    Map<String, WirtschaftsplanungNode> nodes = new HashMap<>();
    DBService service = Einstellungen.getDBService();

    DBIterator<Buchungsart> buchungsartIterator = service.createList(
        Buchungsart.class);
    buchungsartIterator.addFilter("status != 1");
    buchungsartIterator.addFilter("buchungsklasse = ?", buchungsklasse.getID());
    buchungsartIterator.addFilter("art = ?", art);
    while (buchungsartIterator.hasNext())
    {
      Buchungsart buchungsart = buchungsartIterator.next();
      nodes.put(buchungsart.getID(), new WirtschaftsplanungNode(this, buchungsart, art, wirtschaftsplan));
    }

    String sql = "SELECT wirtschaftsplanitem.buchungsart, sum(wirtschaftsplanitem.soll)" +
        "FROM wirtschaftsplanitem, buchungsart " +
        "WHERE wirtschaftsplanitem.buchungsart = buchungsart.id " +
        "AND buchungsart.art = ? " +
        "AND wirtschaftsplanitem.buchungsklasse = ? " +
        "AND wirtschaftsplanitem.wirtschaftsplan = ? " +
        "GROUP BY wirtschaftsplanitem.buchungsart";

    service.execute(sql,
        new Object[] { art, buchungsklasse.getID(), wirtschaftsplan.getID() },
        resultSet -> {
          while (resultSet.next())
          {
            DBIterator<Buchungsart> iterator = service.createList(
                Buchungsart.class);
            iterator.addFilter("id = ?", resultSet.getString(ID_COL));
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
      sql = "SELECT buchung.buchungsart, sum(buchung.betrag) " +
          "FROM buchung, buchungsart " +
          "WHERE buchung.buchungsart = buchungsart.id " +
          "AND buchung.datum >= ? AND buchung.datum <= ? " +
          "AND buchungsart.art = ? " +
          "AND buchung.buchungsklasse = ? " +
          "GROUP BY buchung.buchungsart";
    }
    else
    {
      sql = "SELECT buchung.buchungsart, sum(buchung.betrag) " +
          "FROM buchung, buchungsart " +
          "WHERE buchung.buchungsart = buchungsart.id " +
          "AND buchung.datum >= ? AND buchung.datum <= ? " +
          "AND buchungsart.art = ? " +
          "AND buchungsart.buchungsklasse = ? " +
          "GROUP BY buchung.buchungsart";

    }

    service.execute(sql,
        new Object[] { wirtschaftsplan.getDatumVon(),
            wirtschaftsplan.getDatumBis(), art,
            buchungsklasse.getID() }, resultSet -> {
          while (resultSet.next())
          {
            DBIterator<Buchungsart> iterator = service.createList(
                Buchungsart.class);
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

    children = new ArrayList<>(nodes.values());
  }

  public WirtschaftsplanungNode(WirtschaftsplanungNode parent,
      Buchungsart buchungsart, int art, Wirtschaftsplan wirtschaftsplan)
      throws RemoteException
  {
    type = Type.BUCHUNGSART;
    this.parent = parent;
    this.buchungsart = buchungsart;
    children = new ArrayList<>();
    this.soll = 0;
    ist = 0;

    DBService service = Einstellungen.getDBService();

    if (wirtschaftsplan.isNewObject())
    {
      WirtschaftsplanItem item = service.createObject(WirtschaftsplanItem.class, null);
      item.setBuchungsklasseId(parent.getBuchungsklasse().getID());
      item.setBuchungsartId(buchungsart.getID());
      item.setPosten(buchungsart.getBezeichnung());
      item.setSoll(0);

      children.add(new WirtschaftsplanungNode(this, item));
      return;
    }

    String sql = "SELECT wirtschaftsplanitem.id " +
        "FROM wirtschaftsplanitem, buchungsart " +
        "WHERE wirtschaftsplanitem.buchungsart = buchungsart.id " +
        "AND wirtschaftsplanitem.buchungsart = ? " +
        "AND wirtschaftsplanitem.buchungsklasse = ? " +
        "AND buchungsart.art = ? " +
        "AND wirtschaftsplanitem.wirtschaftsplan = ?";

    service.execute(sql,
        new Object[] { buchungsart.getID(), parent.getBuchungsklasse().getID(),
            art, wirtschaftsplan.getID() }, resultSet -> {
          while (resultSet.next())
          {
            children.add(new WirtschaftsplanungNode(this,
                service.createObject(WirtschaftsplanItem.class,
                    resultSet.getString(1))));
          }

          return children;
        });
  }

  public WirtschaftsplanungNode(WirtschaftsplanungNode parent,
      WirtschaftsplanItem wirtschaftsplanItem) throws RemoteException
  {
    type = Type.POSTEN;
    this.parent = parent;
    this.wirtschaftsplanItem = wirtschaftsplanItem;
    this.soll = wirtschaftsplanItem.getSoll();
    children = null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public GenericIterator getChildren() throws RemoteException
  {
    if (children != null)
    {
      return PseudoIterator.fromArray(children.toArray(new GenericObject[0]));
    }
    return null;
  }

  @Override
  public boolean hasChild(GenericObjectNode genericObjectNode)
      throws RemoteException
  {
    if (!(genericObjectNode instanceof WirtschaftsplanungNode))
    {
      return false;
    }
    return children.contains(genericObjectNode);
  }

  public void addChild(WirtschaftsplanungNode child)
  {
    children.add(child);
  }

  public void removeChild(WirtschaftsplanungNode node)
  {
    children.remove(node);
  }

  @Override
  public GenericObjectNode getParent() throws RemoteException
  {
    return parent;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public GenericIterator getPossibleParents() throws RemoteException
  {
    return null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public GenericIterator getPath() throws RemoteException
  {
    return null;
  }

  @Override
  public Object getAttribute(String s) throws RemoteException
  {
    switch (s)
    {
      case "buchungsklassebezeichnung":
        if (type == Type.BUCHUNGSKLASSE)
        {
          return buchungsklasse.getBezeichnung();
        }
        return "";
      case "buchungsartbezeichnung_posten":
        if (type == Type.BUCHUNGSART)
        {
          return buchungsart.getBezeichnung();
        }
        if (type == Type.POSTEN)
        {
          return wirtschaftsplanItem.getPosten();
        }
        return "";
      case "soll":
        return soll;
      case "ist":
        if (type == Type.POSTEN)
        {
          return "";
        }
        return ist;
      default:
        return null;
    }
  }

  @Override
  public String[] getAttributeNames() throws RemoteException
  {
    return new String[] { "buchungsklassebezeichnung",
        "buchungsartbezeichnung_posten", "soll", "ist" };
  }

  @Override
  public String getID() throws RemoteException
  {
    return null;
  }

  @Override
  public String getPrimaryAttribute() throws RemoteException
  {
    return null;
  }

  @Override
  public boolean equals(GenericObject genericObject) throws RemoteException
  {
    return false;
  }

  public Type getType()
  {
    return type;
  }

  public void setType(Type type)
  {
    this.type = type;
  }

  public Buchungsklasse getBuchungsklasse()
  {
    return buchungsklasse;
  }

  public void setBuchungsklasse(Buchungsklasse buchungsklasse)
  {
    this.buchungsklasse = buchungsklasse;
  }

  public Buchungsart getBuchungsart()
  {
    return buchungsart;
  }

  public void setBuchungsart(Buchungsart buchungsart)
  {
    this.buchungsart = buchungsart;
  }

  public double getSoll()
  {
    return soll;
  }

  public void setSoll(double soll)
  {
    this.soll = soll;
  }

  public double getIst()
  {
    return ist;
  }

  public void setIst(double ist)
  {
    this.ist = ist;
  }

  public WirtschaftsplanItem getWirtschaftsplanItem()
  {
    return wirtschaftsplanItem;
  }

  public void setWirtschaftsplanItem(WirtschaftsplanItem wirtschaftsplanItem)
  {
    this.wirtschaftsplanItem = wirtschaftsplanItem;
  }

  public boolean hasLeaf()
  {
    if (type == Type.POSTEN)
    {
      return true;
    }

    return children.stream().anyMatch(WirtschaftsplanungNode::hasLeaf);
  }

  public int anzahlLeafs()
  {
    if (type == Type.POSTEN)
    {
      return 1;
    }

    if (children.isEmpty())
    {
      return 0;
    }

    return children.stream().mapToInt(WirtschaftsplanungNode::anzahlLeafs)
        .sum();
  }
}
