package exception;

/**
 * Exception khusus yang dilempar saat terjadi input atau data jalan
 * yang tidak valid (misal angka negatif, perempatan tidak ada, dsb).
 */
public class DataTidakValidException extends SistemRambuException {

    public DataTidakValidException(String pesanError) {
        super(pesanError);
    }

    public DataTidakValidException(String pesanError, Throwable cause) {
        super(pesanError, cause);
    }
}
