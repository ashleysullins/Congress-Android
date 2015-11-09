package com.epicodus.knowyourcongressmen.models;

public class Representative {

    private String mName;
    private String mParty;
    private String mGender;
    private String mBirthday;
    private String mPhone;
    private String mOffice;

    public Representative() {
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getParty() {
        return mParty;
    }

    public void setParty(String party) {
        mParty = party;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public String getBirthday() {
        return mBirthday;
    }

    public void setBirthday(String birthday) {
        mBirthday = birthday;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setOffice(String office) {
        mOffice = office;
    }

    public String getOffice() {
        return mOffice;
    }

//    public int getAge() {
//        int year = Integer.parseInt(mBirthday.substring(0,4));
//        int month = Integer.parseInt(mBirthday.substring(5,7));
//        int day = Integer.parseInt(mBirthday.substring(8,10));
//
//
//    }

}
