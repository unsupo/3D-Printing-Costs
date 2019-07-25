package arndt.com.a3d_printing_costs.utilities.easyrecyclerview;

import java.util.Objects;

public class ClickObj<T> {
    Integer id, subId;
    T obj;
    boolean clickable = true;

    public ClickObj(Integer id, Integer subId, T obj, boolean clickable) {
        this.id = id;
        this.subId = subId;
        this.obj = obj;
        this.clickable = clickable;
    }

    public ClickObj(Integer id, T obj, boolean clickable) {
        this.id = id;
        this.obj = obj;
        this.clickable = clickable;
    }

    public ClickObj(Integer id, Integer subId) {
        this.id = id;
        this.subId = subId;
    }

    public ClickObj(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSubId() {
        return subId;
    }

    public void setSubId(Integer subId) {
        this.subId = subId;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClickObj<?> clickObj = (ClickObj<?>) o;
        return Objects.equals(id, clickObj.id) &&
                Objects.equals(subId, clickObj.subId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subId);
    }
}
