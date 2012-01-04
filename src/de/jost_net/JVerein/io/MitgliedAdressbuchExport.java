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

package de.jost_net.JVerein.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.jost_net.JVerein.gui.view.IAuswertung;
import de.jost_net.JVerein.io.Adressbuch.Txt;
import de.jost_net.JVerein.rmi.Mitglied;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

public class MitgliedAdressbuchExport implements IAuswertung
{

  public MitgliedAdressbuchExport()
  {
  }

  public void beforeGo()
  {
    // Nothing to do
  }

  public void go(ArrayList<Mitglied> list, File file, ProgressMonitor monitor)
      throws ApplicationException
  {
    try
    {
      Txt txt = new Txt(file, ";");
      for (Mitglied m : list)
      {
        txt.add(m);
      }
      txt.close();
      monitor.setStatusText("Auswertung fertig. " + list.size() + " S�tze.");
    }
    catch (IOException e)
    {
      throw new ApplicationException(e);
    }
  }

  public String getDateiname()
  {
    return "adressbuchexport";
  }

  public String getDateiendung()
  {
    return "CSV";
  }

  public boolean openFile()
  {
    return true;
  }

  public String toString()
  {
    return "Adressbuchexport CSV";
  }
}