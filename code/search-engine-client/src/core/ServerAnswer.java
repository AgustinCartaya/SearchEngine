package core;

public class ServerAnswer {
    
    private String answerType;
    private String answerContent;
    
    public ServerAnswer(String answerType, String answerContent){
        this.answerType = answerType;
        this.answerContent = answerContent;
    }
    
    public void setAnswer(String answerType, String answerContent){
        this.setAnswerType(answerType);
        this.setAnswerContent(answerContent);
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }
    
    public boolean isError(){
        return this.answerType.contains("ERROR");
    }
    
}
