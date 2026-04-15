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
package de.jost_net.JVerein.server;

import java.rmi.RemoteException;
import java.util.Date;

import de.jost_net.JVerein.rmi.Kassenzaehlprotokoll;
import de.jost_net.JVerein.rmi.Konto;

public class KassenzaehlprotokollImpl extends AbstractJVereinDBObject
    implements Kassenzaehlprotokoll
{
  public KassenzaehlprotokollImpl() throws RemoteException
  {
    super();
  }

  @Override
  public String getObjektName() throws RemoteException
  {
    return "Kassenzählprotokoll";
  }

  @Override
  public String getObjektNameMehrzahl() throws RemoteException
  {
    return "Kassenzählprotokolle";
  }

  @Override
  protected String getTableName()
  {
    return "kassenzaehlprotokoll";
  }

  @Override
  public String getPrimaryAttribute() throws RemoteException
  {
    return "id";
  }

  @Override
  protected Class<?> getForeignObject(String field) throws RemoteException
  {
    if (field == null)
      return null;
    return field.equals("konto") ? Konto.class : null;
  }

  @Override
  public void setId(String id) throws RemoteException
  {
    this.setID(id);
    setAttribute("id", id);
  }

  @Override
  public String getBezeichnung() throws RemoteException
  {
    return (String) getAttribute("bezeichnung");
  }

  @Override
  public void setBezeichnung(String bezeichnung) throws RemoteException
  {
    setAttribute("bezeichnung", bezeichnung);
  }

  @Override
  public Konto getKonto() throws RemoteException
  {
    Object k = getAttribute("konto");
    if (k == null)
      return null;

    Cache cache = Cache.get(Konto.class, true);
    return (Konto) cache.get(k);
  }

  @Override
  public void setKontoId(String kontoId) throws RemoteException
  {
    setAttribute("konto", kontoId);
  }

  @Override
  public Date getDatum() throws RemoteException
  {
    return (Date) getAttribute("datum");
  }

  @Override
  public void setDatum(Date datum) throws RemoteException
  {
    setAttribute("datum", datum);
  }

  @Override
  public int get1Cent() throws RemoteException
  {
    return (int) getAttribute("ein_cent");
  }

  @Override
  public void set1Cent(int anzahl) throws RemoteException
  {
    setAttribute("ein_cent", anzahl);
  }

  @Override
  public int get2Cent() throws RemoteException
  {
    return (int) getAttribute("zwei_cent");
  }

  @Override
  public void set2Cent(int anzahl) throws RemoteException
  {
    setAttribute("zwei_cent", anzahl);
  }

  @Override
  public int get5Cent() throws RemoteException
  {
    return (int) getAttribute("fuenf_cent");
  }

  @Override
  public void set5Cent(int anzahl) throws RemoteException
  {
    setAttribute("fuenf_cent", anzahl);
  }

  @Override
  public int get10Cent() throws RemoteException
  {
    return (int) getAttribute("zehn_cent");
  }

  @Override
  public void set10Cent(int anzahl) throws RemoteException
  {
    setAttribute("zehn_cent", anzahl);
  }

  @Override
  public int get20Cent() throws RemoteException
  {
    return (int) getAttribute("zwanzig_cent");
  }

  @Override
  public void set20Cent(int anzahl) throws RemoteException
  {
    setAttribute("zwanzig_cent", anzahl);
  }

  @Override
  public int get50Cent() throws RemoteException
  {
    return (int) getAttribute("fuenfzig_cent");
  }

  @Override
  public void set50Cent(int anzahl) throws RemoteException
  {
    setAttribute("fuenfzig_cent", anzahl);
  }

  @Override
  public int get1Euro() throws RemoteException
  {
    return (int) getAttribute("ein_euro");
  }

  @Override
  public void set1Euro(int anzahl) throws RemoteException
  {
    setAttribute("ein_euro", anzahl);
  }

  @Override
  public int get2Euro() throws RemoteException
  {
    return (int) getAttribute("zwei_euro");
  }

  @Override
  public void set2Euro(int anzahl) throws RemoteException
  {
    setAttribute("zwei_euro", anzahl);
  }

  @Override
  public int get5Euro() throws RemoteException
  {
    return (int) getAttribute("fuenf_euro");
  }

  @Override
  public void set5Euro(int anzahl) throws RemoteException
  {
    setAttribute("fuenf_euro", anzahl);
  }

  @Override
  public int get10Euro() throws RemoteException
  {
    return (int) getAttribute("zehn_euro");
  }

  @Override
  public void set10Euro(int anzahl) throws RemoteException
  {
    setAttribute("zehn_euro", anzahl);
  }

  @Override
  public int get20Euro() throws RemoteException
  {
    return (int) getAttribute("zwanzig_euro");
  }

  @Override
  public void set20Euro(int anzahl) throws RemoteException
  {
    setAttribute("zwanzig_euro", anzahl);
  }

  @Override
  public int get50Euro() throws RemoteException
  {
    return (int) getAttribute("fuenfzig_euro");
  }

  @Override
  public void set50Euro(int anzahl) throws RemoteException
  {
    setAttribute("fuenfzig_euro", anzahl);
  }

  @Override
  public int get100Euro() throws RemoteException
  {
    return (int) getAttribute("hundert_euro");
  }

  @Override
  public void set100Euro(int anzahl) throws RemoteException
  {
    setAttribute("hundert_euro", anzahl);
  }

  @Override
  public int get200Euro() throws RemoteException
  {
    return (int) getAttribute("zweihundert_euro");
  }

  @Override
  public void set200Euro(int anzahl) throws RemoteException
  {
    setAttribute("zweihundert_euro", anzahl);
  }

  @Override
  public int get500Euro() throws RemoteException
  {
    return (int) getAttribute("fuenfhundert_euro");
  }

  @Override
  public void set500Euro(int anzahl) throws RemoteException
  {
    setAttribute("fuenfhundert_euro", anzahl);
  }

  @Override
  public String getZaehler() throws RemoteException
  {
    return (String) getAttribute("zaehler");
  }

  @Override
  public void setZaehler(String zaehler) throws RemoteException
  {
    setAttribute("zaehler", zaehler);
  }
}
