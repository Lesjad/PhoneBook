package leszekJadacki.phonebook;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class AppResponseBody {

    private final String message;
    private final int status;
    private final LocalDateTime timestamp;
    private final String path;
    private final Collection<?> content;

    private AppResponseBody(AppResponseBodyBuilder responseBuilder){
        this.message = responseBuilder.message;
        this.status = responseBuilder.status;
        this.timestamp = responseBuilder.timestamp;
        this.path = responseBuilder.path;
        this.content = responseBuilder.content;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public static class AppResponseBodyBuilder{
        private final String message;
        private int status;
        private LocalDateTime timestamp = LocalDateTime.now();
        private String path;
        private Collection<?> content;

        public AppResponseBodyBuilder(String message) {
            this.message = message;
        }

        public AppResponseBodyBuilder status(int status){
            this.status = status;
            return this;
        }

        public AppResponseBodyBuilder timestamp(LocalDateTime timestamp){
            this.timestamp = timestamp;
            return this;
        }

        public AppResponseBodyBuilder path(String path){
            this.path = path;
            return this;
        }

        public AppResponseBodyBuilder content(Collection<?> content){
            this.content = content;
            return this;
        }

        public AppResponseBody build(){
            return new AppResponseBody(this);
        }
    }
}
