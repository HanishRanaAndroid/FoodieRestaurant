package com.valle.resturantfoodieapp.models;

import java.util.List;

public class MenuItemListModel {

    /*{
        "status": "success",
            "response": {
        "msg": "Item Found!",
                "Items": [
        {
            "Id": "5",
                "Rest_Id": "77",
                "Item_Name": "VEG MANCHURIAN GRAVY",
                "Item_Category": "Chinese",
                "Item_Price": "1200",
                "Item_Description": "Lorem Ipsam",
                 "Item_Status": "Out Of Stock",
                "Item_Image": "",
                "Created_Date": "2019-12-12 03:45:07"
        }
        ]
    }
    }*/

    public responseData response;

    public class responseData {
        public List<ItemsData> Items;
        public class ItemsData{
            public String Id;
            public String Rest_Id;
            public String Item_Name;
            public String Item_Category;
            public String Item_Price;
            public String Item_Description;
            public String Item_Status;
            public String Item_Image;
            public String Item_Flavor;
            public String Created_Date;
        }

    }

}
