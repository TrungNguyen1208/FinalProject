package ptit.nttrung.finalproject.base;


class ViewNotAttachedException extends RuntimeException {
    ViewNotAttachedException() {
        super("Please call attachView() before proceeding!");
    }
}