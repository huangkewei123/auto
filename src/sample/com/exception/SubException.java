package sample.com.exception;

public class SubException extends Exception {
    /*无参构造函数*/
    public SubException(){
        super();
    }
    
    //用详细信息指定一个异常
    public SubException(String message){
        super(message);
    }
    
    //用指定的详细信息和原因构造一个新的异常
    public SubException(String message, Throwable cause){
        super(message,cause);
    }
    
    //用指定原因构造一个新的异常
    public SubException(Throwable cause) {
        super(cause);
    }
}