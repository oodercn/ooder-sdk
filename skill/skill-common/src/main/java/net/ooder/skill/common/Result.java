package net.ooder.skill.common;

import java.util.Optional;

public interface Result<T> {
    
    boolean isSuccess();
    
    T getData();
    
    String getError();
    
    int getCode();
    
    default Optional<T> getDataOptional() {
        return Optional.ofNullable(getData());
    }
    
    static <T> Result<T> success(T data) {
        return new Result<>() {
            @Override
            public boolean isSuccess() {
                return true;
            }
            
            @Override
            public T getData() {
                return data;
            }
            
            @Override
            public String getError() {
                return null;
            }
            
            @Override
            public int getCode() {
                return 0;
            }
        };
    }
    
    static <T> Result<T> failure(String error) {
        return failure(error, -1);
    }
    
    static <T> Result<T> failure(String error, int code) {
        return new Result<>() {
            @Override
            public boolean isSuccess() {
                return false;
            }
            
            @Override
            public T getData() {
                return null;
            }
            
            @Override
            public String getError() {
                return error;
            }
            
            @Override
            public int getCode() {
                return code;
            }
        };
    }
    
    static <T> Result<T> error(String error) {
        return failure(error, -1);
    }
}
