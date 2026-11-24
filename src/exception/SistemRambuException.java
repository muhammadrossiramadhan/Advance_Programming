package exception;

/**
 * Superclass (Checked Exception) dasar untuk seluruh kesalahan pada
 * Sistem Rambu Informasi Dinamis Perkotaan.
 * Seluruh custom exception dalam sistem rambu wajib menginduk ke kelas ini.
 */
public class SistemRambuException extends Exception {

    public SistemRambuException(String pesanError) {
        super(pesanError);
    }

    public SistemRambuException(String pesanError, Throwable cause) {
        super(pesanError, cause);
    }
}
