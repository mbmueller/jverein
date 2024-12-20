/**********************************************************************
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
package de.jost_net.JVerein.keys;

public enum Kontoart
{
  // LIMIT ist keine Kontoart, sondern dient zur Abgrenzung.
  // Ids unter dem Limit werden regul�r im Buchungsklassensaldo, Kontensaldo und der Wirtschaftsplanung
  // ber�cksichtigt.
  // Ids �ber dem Limit werden in diesen Salden ignoriert.
  GELD(1, "Geldkonto"),
  ANLAGE(2, "Anlagenkonto"),
  TRANSFER(3, "Transferkonto"),
  LIMIT(100, "-- Limit --"),
  RUECKLAGE(101, "R�cklagenkonto");

  private final String text;

  private final int key;
  
  Kontoart(int key, String text)
  {
    this.key = key;
    this.text = text;
  }

  public int getKey()
  {
    return key;
  }

  public String getText()
  {
    return text;
  }

  public static Kontoart getByKey(int key)
  {
    for (Kontoart art : Kontoart.values())
    {
      if (art.getKey() == key)
      {
        return art;
      }
    }
    return null;
  }

  @Override
  public String toString()
  {
    return getText();
  }
}
