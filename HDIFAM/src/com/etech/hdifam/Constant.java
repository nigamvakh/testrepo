package com.etech.hdifam;

public class Constant {
	public static String query_select_her_date = "SELECT * FROM her_date_master WHERE start_date <= '%s' AND end_date >= '%s';";
	public static String query_select_his_date = "SELECT * FROM his_date_master WHERE start_date <= '%s' AND end_date >= '%s';";
	public static String query_select_result = "SELECT * FROM result_master WHERE her_date_id=%d AND his_date_id=%d;";
	public static String App_Id = "148074838669352";
	//public static String flurry_Appid = "88V3TBJ6BC5QJDZX2TN7";
	public static String flurry_Appid = "KBWG6WMDRRWJWQ9KTCG3";
}
