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
package de.jost_net.JVerein.rmi;

import java.rmi.RemoteException;
import java.util.Date;

public interface Kassenzaehlprotokoll extends JVereinDBObject
{
  void setId(String id) throws RemoteException;

  String getBezeichnung() throws RemoteException;

  void setBezeichnung(String bezeichnung) throws RemoteException;

  Konto getKonto() throws RemoteException;

  void setKontoId(String kontoId) throws RemoteException;

  Date getDatum() throws RemoteException;

  void setDatum(Date datum) throws RemoteException;

  int get1Cent() throws RemoteException;

  void set1Cent(int anzahl) throws RemoteException;

  int get2Cent() throws RemoteException;

  void set2Cent(int anzahl) throws RemoteException;

  int get5Cent() throws RemoteException;

  void set5Cent(int anzahl) throws RemoteException;

  int get10Cent() throws RemoteException;

  void set10Cent(int anzahl) throws RemoteException;

  int get20Cent() throws RemoteException;

  void set20Cent(int anzahl) throws RemoteException;

  int get50Cent() throws RemoteException;

  void set50Cent(int anzahl) throws RemoteException;

  int get1Euro() throws RemoteException;

  void set1Euro(int anzahl) throws RemoteException;

  int get2Euro() throws RemoteException;

  void set2Euro(int anzahl) throws RemoteException;

  int get5Euro() throws RemoteException;

  void set5Euro(int anzahl) throws RemoteException;

  int get10Euro() throws RemoteException;

  void set10Euro(int anzahl) throws RemoteException;

  int get20Euro() throws RemoteException;

  void set20Euro(int anzahl) throws RemoteException;

  int get50Euro() throws RemoteException;

  void set50Euro(int anzahl) throws RemoteException;

  int get100Euro() throws RemoteException;

  void set100Euro(int anzahl) throws RemoteException;

  int get200Euro() throws RemoteException;

  void set200Euro(int anzahl) throws RemoteException;

  int get500Euro() throws RemoteException;

  void set500Euro(int anzahl) throws RemoteException;

  String getZaehler() throws RemoteException;

  void setZaehler(String zaehler) throws RemoteException;
}
