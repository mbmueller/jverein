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
package de.jost_net.JVerein.server.DDLTool.Updates;

import java.sql.Connection;

import de.jost_net.JVerein.server.DDLTool.AbstractDDLUpdate;
import de.jost_net.JVerein.server.DDLTool.Column;
import de.jost_net.JVerein.server.DDLTool.Index;
import de.jost_net.JVerein.server.DDLTool.Table;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

public class Update0507 extends AbstractDDLUpdate
{
  public Update0507(String driver, ProgressMonitor monitor, Connection conn)
  {
    super(driver, monitor, conn);
  }

  @Override
  public void run() throws ApplicationException
  {
    Table kassenzaehlprotokoll = new Table("kassenzaehlprotokoll");

    Column id = new Column("id", COLTYPE.BIGINT, 4, null, false, false);
    kassenzaehlprotokoll.add(id);
    kassenzaehlprotokoll.setPrimaryKey(id);
    kassenzaehlprotokoll.add(
        new Column("bezeichnung", COLTYPE.VARCHAR, 255, null, false, false));
    Column konto = new Column("konto", COLTYPE.INTEGER, 255, null, false,
        false);
    kassenzaehlprotokoll.add(konto);
    kassenzaehlprotokoll
        .add(new Column("datum", COLTYPE.DATE, 10, null, false, false));

    kassenzaehlprotokoll
        .add(new Column("ein_cent", COLTYPE.INTEGER, 4, null, false, false));
    kassenzaehlprotokoll
        .add(new Column("zwei_cent", COLTYPE.INTEGER, 4, null, false, false));
    kassenzaehlprotokoll
        .add(new Column("fuenf_cent", COLTYPE.INTEGER, 4, null, false, false));
    kassenzaehlprotokoll
        .add(new Column("zehn_cent", COLTYPE.INTEGER, 4, null, false, false));
    kassenzaehlprotokoll.add(
        new Column("zwanzig_cent", COLTYPE.INTEGER, 4, null, false, false));
    kassenzaehlprotokoll.add(
        new Column("fuenfzig_cent", COLTYPE.INTEGER, 4, null, false, false));
    kassenzaehlprotokoll
        .add(new Column("ein_euro", COLTYPE.INTEGER, 4, null, false, false));
    kassenzaehlprotokoll
        .add(new Column("zwei_euro", COLTYPE.INTEGER, 4, null, false, false));
    kassenzaehlprotokoll
        .add(new Column("fuenf_euro", COLTYPE.INTEGER, 4, null, false, false));
    kassenzaehlprotokoll
        .add(new Column("zehn_euro", COLTYPE.INTEGER, 4, null, false, false));
    kassenzaehlprotokoll.add(
        new Column("zwanzig_euro", COLTYPE.INTEGER, 4, null, false, false));
    kassenzaehlprotokoll.add(
        new Column("fuenfzig_euro", COLTYPE.INTEGER, 4, null, false, false));
    kassenzaehlprotokoll.add(
        new Column("hundert_euro", COLTYPE.INTEGER, 4, null, false, false));
    kassenzaehlprotokoll.add(
        new Column("zweihundert_euro", COLTYPE.INTEGER, 4, null, false, false));
    kassenzaehlprotokoll.add(new Column("fuenfhundert_euro", COLTYPE.INTEGER, 4,
        null, false, false));

    kassenzaehlprotokoll
        .add(new Column("zaehler", COLTYPE.VARCHAR, 255, null, false, false));

    execute(createTable(kassenzaehlprotokoll));

    Index idx = new Index("idx_kassenzaehlprotokoll", false);
    idx.add(konto);
    execute(idx.getCreateIndex("kassenzaehlprotokoll"));

    execute((createForeignKey("fk_kassenzaehlprotokoll_konto",
        "kassenzaehlprotokoll", "konto", "konto", "id", "SET NULL",
        "NO ACTION")));
  }
}
