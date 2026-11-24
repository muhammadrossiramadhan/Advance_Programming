package exception;

/**
 * Exception yang dilempar saat algoritma pencarian rute tidak menemukan
 * jalur penghubung antara perempatan asal dan tujuan.
 */
public class RuteTidakDitemukanException extends SistemRambuException {

    public RuteTidakDitemukanException(String pesanError) {
        super(pesanError);
    }

    public RuteTidakDitemukanException(String pesanError, Throwable cause) {
        super(pesanError, cause);
    }
}
