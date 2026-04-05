package net.ooder.bpm.client;


import net.ooder.annotation.HttpMethod;
import net.ooder.annotation.RequestType;
import net.ooder.annotation.ResponseType;

public interface ActivityDefService {


    /***
     *
     * @return
     */

    public String getUrl() ;

    /**
     *
     * @return
     */
    public RequestType getRequestType();

    /**
     *
     * @return
     */
    public ResponseType getResponseType();

    /**
     *
     * @return
     */
    public HttpMethod getMethod();

    /**
     *
     * @return
     */
    public String  getServiceParams() ;


    /**
     *
     * @return
     */
    public String getServiceSelectedID();

}
