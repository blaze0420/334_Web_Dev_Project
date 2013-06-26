package client;

public class errorBean {
    private boolean flag = false;
    private String error;
    
    errorBean(){ error=""; }
    errorBean(String error){
        if(error!=""){
         this.error = error;
         flag=true;
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
