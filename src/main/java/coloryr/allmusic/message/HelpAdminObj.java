package coloryr.allmusic.message;

public class HelpAdminObj {
    public String Reload;
    public String Next;
    public String Ban;
    public String Url;
    public String Delete;
    public String AddList;
    public String ClearList;
    public String Login;

    public HelpAdminObj() {
        Reload = "§d[AllMusic]§2使用/music reload 重读配置文件";
        Next = "§d[AllMusic]§2使用/music next 强制切歌";
        Ban = "§d[AllMusic]§2使用/music ban [ID] 禁止点这首歌";
        Url = "§d[AllMusic]§2使用/music url [url] 播放一首自定义歌曲";
        Delete = "§d[AllMusic]§2使用/music delete [序号] 删除队列中的歌曲";
        AddList = "§d[AllMusic]§2使用/music addlist [歌单ID] 添加歌单到空闲列表";
        ClearList = "§d[AllMusic]§2使用/music clearlist 清空空闲歌单";
        Login = "§d[AllMusic]§2使用/music login 重新登录账户";
    }

    public boolean check() {
        if (Reload == null)
            return true;
        if (Next == null)
            return true;
        if (Ban == null)
            return true;
        if (Url == null)
            return true;
        if (Delete == null)
            return true;
        if (AddList == null)
            return true;
        if (ClearList == null)
            return true;
        if (Login == null)
            return true;

        return false;
    }
}
