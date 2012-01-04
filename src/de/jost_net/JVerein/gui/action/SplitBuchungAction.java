/**********************************************************************
 * $Source$
 * $Revision$
 * $Date$
 * $Author$
 *
 * Copyright (c) by Heiner Jostkleigrewe
 * This program is free software: you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,  but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
 *  the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, 
 * see <http://www.gnu.org/licenses/>.
 * 
 * heiner@jverein.de
 * www.jverein.de
 **********************************************************************/
package de.jost_net.JVerein.gui.action;

import java.rmi.RemoteException;

import de.jost_net.JVerein.JVereinPlugin;
import de.jost_net.JVerein.gui.view.SplitBuchungView;
import de.jost_net.JVerein.rmi.Buchung;
import de.jost_net.JVerein.rmi.Jahresabschluss;
import de.jost_net.JVerein.util.JVDateFormatTTMMJJJJ;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class SplitBuchungAction implements Action
{
  public void handleAction(Object context) throws ApplicationException
  {
    Buchung b = null;

    if (context != null && (context instanceof Buchung))
    {
      b = (Buchung) context;
      try
      {
        Jahresabschluss ja = b.getJahresabschluss();
        if (ja != null)
        {
          throw new ApplicationException(JVereinPlugin.getI18n().tr(
              "Buchung wurde bereits am {0} von {1} abgeschlossen.",
              new String[] { new JVDateFormatTTMMJJJJ().format(ja.getDatum()),
                  ja.getName() }));
        }
        if (b.getBuchungsart() == null)
        {
          throw new ApplicationException(
              "Der Buchung muss zun�chst eine Buchungsart zugeordnet werden.");
        }

      }
      catch (RemoteException e)
      {
        throw new ApplicationException(e.getMessage());
      }
    }
    else
    {
      throw new ApplicationException(
          JVereinPlugin
              .getI18n()
              .tr("Programmfehler! Splitbuchung muss mit einer existierenden Buchung aufgerufen werden"));
    }
    GUI.startView(SplitBuchungView.class.getName(), b);
  }
}