package de.jost_net.JVerein.gui.action;

import de.jost_net.JVerein.DBTools.DBTransaction;
import de.jost_net.JVerein.rmi.Wirtschaftsplan;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.gui.util.SWTUtil;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class WirtschaftsplanDeleteAction implements Action
{
  @Override
  public void handleAction(Object context) throws ApplicationException
  {
    Wirtschaftsplan[] wirtschaftsplaene;
    if (context instanceof Wirtschaftsplan)
    {
      wirtschaftsplaene = new Wirtschaftsplan[] { ((Wirtschaftsplan) context) };
    }
    else if (context instanceof Wirtschaftsplan[])
    {
      wirtschaftsplaene = (Wirtschaftsplan[]) context;
    }
    else
    {
      throw new ApplicationException("Kein Wirtschaftsplan ausgew�hlt");
    }

    String mehrzahl = wirtschaftsplaene.length > 1 ? "Wirtschaftspl�ne" : "Wirtschaftsplan";
    YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
    d.setTitle(mehrzahl + " l�schen");
    d.setPanelText(mehrzahl + " l�schen?");
    d.setSideImage(SWTUtil.getImage("dialog-warning-large.png"));
    String text = "Wollen Sie diese" + (wirtschaftsplaene.length > 1 ? "":"s") + " " + mehrzahl + " wirklich l�schen?"
        + "\nDiese Daten k�nnen nicht wieder hergestellt werden!";
    d.setText(text);

    try
    {
      if (!((Boolean) d.open()))
      {
        return;
      }
    }
    catch (Exception e)
    {
      Logger.error("Fehler beim L�schen des Wirtschaftsplans", e);
      return;
    }

    DBTransaction.starten();

    try
    {
      for (Wirtschaftsplan wirtschaftsplan : wirtschaftsplaene)
      {
        wirtschaftsplan.delete();
      }
      DBTransaction.commit();
      GUI.getStatusBar().setSuccessText(mehrzahl + " gel�scht!");
    }
    catch (Exception e)
    {
      GUI.getStatusBar().setErrorText("Fehler beim L�schen!");
      Logger.error("Fehler beim L�schen des Wirtschaftsplans", e);
      DBTransaction.rollback();
    }
  }
}
