package idoandroid.model;

/**
 * Created by KevinChris on 23-Aug-15.
 */
public class Contact {
    private int Id;
    private String Name;
    private long PhoneNo;
    private Boolean IsSynced;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public long getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(long phoneNo) {
        PhoneNo = phoneNo;
    }

    public Boolean getIsSynced() {
        return IsSynced;
    }

    public void setIsSynced(Boolean isSynced) {
        IsSynced = isSynced;
    }
}
