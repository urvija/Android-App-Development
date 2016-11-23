package cs646.coolcab;

public class UserData {

    private String mName;
    private String mPhone;
    private String mToAddress;
    private String mFromAddress;
    private String mDate;
    private String mTime;

    public UserData() {}

    public UserData(String name, String number, String toAddress, String fromAddress, String
            date, String time) {
        this.mName = name;
        this.mPhone = number;
        this.mToAddress = toAddress;
        this.mFromAddress = fromAddress;
        this.mDate = date;
        this.mTime = time;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getToAddress() {
        return mToAddress;
    }

    public void setToAddress(String toAddress) {
        mToAddress = toAddress;
    }

    public String getFromAddress() {
        return mFromAddress;
    }

    public void setFromAddress(String fromAddress) {
        mFromAddress = fromAddress;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }
}
