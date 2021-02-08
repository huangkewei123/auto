package sample.com.constants;

import java.util.List;

public class Entity {
    private Integer line;           //行号
    private Integer pline;          //父行号
    private String handleName;      //函数名称
    private String parameter;       //函数参数
    private String type;            //函数类型      if？还是elif？还是else？或者while？normal？
    private String attribution;     //函数归属类型，比如当前待执行的方法归属if？还是elif？还是else？或者while？
    private Integer level;          //层级
    private List<Entity> subList;   //子块
    private Boolean haveSub;        //是否有下级，可以判断sublist是否为空，但是有个简单的变量取值做判断会更方便，默认值为false

    public Entity(){
        this.setHaveSub(false);
    }

    public Entity(Integer line, String handleName , String parameter , String type , String attribution
            ,Integer level ,List<Entity> list, Boolean haveSub){
        this.level = level;
        this.handleName = handleName;
        this.line = line;
        this.parameter = parameter;
        this.type = type;
        this.attribution = attribution;
        this.subList = list;
        this.haveSub = haveSub;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<Entity> getSubList() {
        return subList;
    }

    public void setSubList(List<Entity> subList) {
        this.subList = subList;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public String getHandleName() {
        return handleName;
    }

    public void setHandleName(String handleName) {
        this.handleName = handleName;
    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public Boolean getHaveSub() {
        return haveSub;
    }

    public void setHaveSub(Boolean haveSub) {
        this.haveSub = haveSub;
    }

    public Integer getPline() {
        return pline;
    }

    public void setPline(Integer pline) {
        this.pline = pline;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "line=" + line +
                ", pline=" + pline +
                ", handleName='" + handleName + '\'' +
                ", parameter='" + parameter + '\'' +
                ", type='" + type + '\'' +
                ", attribution='" + attribution + '\'' +
                ", level=" + level +
                ", subList=" + subList +
                ", haveSub=" + haveSub +
                '}';
    }
}
