package idoandroid.model;

/**
 * Created by KevinChris on 23-Aug-15.
 */
public class Contact {
    private int Id;
    private String Name;
    private long PhoneNo;
    private int IsSynced;

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

    public int getIsSynced() {
        return IsSynced;
    }

    public void setIsSynced(int isSynced) {
        IsSynced = isSynced;
    }
}
