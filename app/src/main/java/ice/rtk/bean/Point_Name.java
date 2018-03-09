package ice.rtk.bean;

/**
 * Created by Administrator on 2018/1/25.
 */

public class Point_Name {
    private String name;

    public Point_Name(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Point_Name{" +
                "name='" + name + '\'' +
                '}';
    }
}
