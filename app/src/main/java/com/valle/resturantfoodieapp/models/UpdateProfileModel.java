package com.valle.resturantfoodieapp.models;

public class UpdateProfileModel {
   /* {
        "status": "success",
            "response": {
        "status": "success",
                "msg": "User updated successfully!",
                "response": {
            "User_Id": "78",
                    "Full_Name": "Developer",
                    "Restaurant_Name": "DeveloperRest",
                    "Email": "test@blissitsolutions.com",
                    "Mobile": "9876543210",
                    "Password": "$2y$10$0pLWZe6NeXkn6wCfH6sy/.7X6F0x9u4WJyhsZWyTmINXnnNVAmHf2",
                    "City": "Panchkula",
                    "Address": "Sector 51",
                    "Website": null,
                    "Latitude": "30.727921",
                    "Longitude": "76.839661",
                    "Role": "Restaurant",
                    "Login_Type": "Default",
                    "Profile_Image": null,
                    "Cover_Image": null,
                    "Refer_Code": "BL6990",
                    "Validation_Code": "",
                    "Is_Active": "Active",
                    "Device_Token": "345",
                    "createdDtm": "2019-12-11 09:50:46"
        }
    }
    }*/

   public responseData response;

   public class responseData{
       public String msg;
       public LoginModel.responseData.UserInfoData response;

       public class responseUserInfo{
           public String User_Id;
           public String Full_Name;
           public String Restaurant_Name;
           public String Email;
           public String Mobile;
           public String City;
           public String Address;
           public String Latitude;
           public String Longitude;
           public String Role;
           public String Login_Type;
           public String Profile_Image;
           public String Cover_Image;
           public String Refer_Code;
           public String Is_Active;
           public String Device_Token;
           public String createdDtm;
       }
   }


}
