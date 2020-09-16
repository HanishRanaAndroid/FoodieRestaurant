package com.valle.resturantfoodieapp.models;

import java.util.List;

public class CategoryListModel {
    /*{
        "status": "success",
            "response": {
        "msg": "Categories Found!",
                "Items": [
        {
            "Id": "1",
                "Category_Name": "SOUPS"
        },
        {
            "Id": "2",
                "Category_Name": "Chinese"
        },
        {
            "Id": "3",
                "Category_Name": "BREAKFAST"
        },
        {
            "Id": "4",
                "Category_Name": "LUNCH"
        },
        {
            "Id": "5",
                "Category_Name": "DINNER"
        },
        {
            "Id": "6",
                "Category_Name": "SNAKS"
        }
        ]
    }
    }*/

    public responseData response;

    public class responseData {
        public List<ItemsList> Items;

        public class ItemsList {
            public String Id;
            public String Category_Name;
        }
    }
}
