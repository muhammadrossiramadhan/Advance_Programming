package exception;

public class DataTidakValidException extends Exception {
    public DataTidakValidException(String pesanError) {
        super(pesanError);
    }
}
