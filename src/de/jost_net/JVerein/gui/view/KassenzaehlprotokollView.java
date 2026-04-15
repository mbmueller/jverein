/**********************************************************************
 * Kopie aus Hibiscus
 * Copyright (c) by willuhn software & services
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
package de.jost_net.JVerein.gui.view;

import de.jost_net.JVerein.gui.control.KassenzaehlprotokollControl;
import de.jost_net.JVerein.gui.control.Savable;
import de.jost_net.JVerein.rmi.Kassenzaehlprotokoll;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.util.ApplicationException;

public class KassenzaehlprotokollView extends AbstractDetailView
{
  KassenzaehlprotokollControl control;

  @Override
  public void bind() throws Exception
  {
    if (!(this.getCurrentObject() instanceof Kassenzaehlprotokoll))
    {
      throw new ApplicationException(
          "Fehler beim Anzeigen des Kassenzählprotokolls");
    }

    Kassenzaehlprotokoll protokoll = (Kassenzaehlprotokoll) this
        .getCurrentObject();

    GUI.getView().setTitle("Kassenzählprotokoll");

    control = new KassenzaehlprotokollControl(this);

    LabelGroup allgemein = new LabelGroup(this.getParent(), "Allgemein");

    SimpleContainer layout = new SimpleContainer(allgemein.getComposite());

    TextInput bezeichnung = new TextInput(protokoll.getBezeichnung());

    DialogInput konto = control.getKonto(true);

    DateInput datum = new DateInput(protokoll.getDatum());

    TextInput zaehler = new TextInput(protokoll.getZaehler());

    layout.addLabelPair("Bezeichnung", bezeichnung);
    layout.addLabelPair("Konto", konto);
    layout.addLabelPair("Datum", datum);
    layout.addLabelPair("Gezählt von", zaehler);
  }

  @Override
  protected Savable getControl()
  {
    return control;
  }
}
