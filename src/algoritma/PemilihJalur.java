package algoritma;

import exception.DataTidakValidException;
import java.util.LinkedList;
import model.Perempatan;
import model.PetaKota;

public interface PemilihJalur {
    LinkedList<Perempatan> cariRute(PetaKota peta, String kodeAsal, String kodeTujuan)
            throws DataTidakValidException;
}
