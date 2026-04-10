package net.ooder.common;

public interface Result<T> {
	public T getData() ;
	
	public void setData(T data) ;

	public int getRequestStatus() ;

	public void setRequestStatus(int requestStatus) ;
}
