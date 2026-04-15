package de.jost_net.JVerein.gui.control;

import java.rmi.RemoteException;

import de.jost_net.JVerein.Einstellungen;
import de.jost_net.JVerein.gui.input.KontoauswahlInput;
import de.jost_net.JVerein.rmi.JVereinDBObject;
import de.jost_net.JVerein.rmi.Kassenzaehlprotokoll;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.util.ApplicationException;

public class KassenzaehlprotokollControl extends VorZurueckControl
    implements Savable
{
  DialogInput konto;

  Kassenzaehlprotokoll protokoll;

  public KassenzaehlprotokollControl(AbstractView view)
  {
    super(view);
  }

  @Override
  public JVereinDBObject prepareStore()
      throws RemoteException, ApplicationException
  {
    return null;
  }

  @Override
  public void handleStore() throws ApplicationException
  {

  }

  public Kassenzaehlprotokoll getProtokoll() throws RemoteException
  {
    if (protokoll != null)
    {
      return protokoll;
    }

    protokoll = (Kassenzaehlprotokoll) getCurrentObject();
    if (protokoll == null)
    {
      protokoll = Einstellungen.getDBService()
          .createObject(Kassenzaehlprotokoll.class, null);
    }
    return protokoll;
  }

  public DialogInput getKonto(boolean withFocus) throws RemoteException
  {
    if (konto != null)
    {
      return konto;
    }

    konto = new KontoauswahlInput(getProtokoll().getKonto()).getKontoAuswahl(
        false, getProtokoll().getKonto().getID(), false, true,
        BuchungsControl.Kontenfilter.GELDKONTO);
    if (withFocus)
    {
      konto.focus();
    }
    return konto;
  }
}
