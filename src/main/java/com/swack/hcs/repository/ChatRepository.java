package com.swack.hcs.repository;

import com.swack.hcs.bean.ChatLog;
import com.swack.hcs.bean.Room;
import com.swack.hcs.util.AppConstants;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * チャットリポジトリ
 */
@Repository
public class ChatRepository {

  @Autowired
  private NamedParameterJdbcTemplate jdbc;

  /**
   * チャットログリスト取得.
   *
   * @param roomId 部屋ID
   * @return チャットログリスト
   */
  public List<ChatLog> getChatlogList(String roomId) {
    final String sql = "SELECT CHATLOGID, U.USERID AS USERID, U.USERNAME AS USERNAME, U.USERIMGPATH AS USERIMGPATH, MESSAGE, CREATED_AT FROM CHATLOG C JOIN USERS U ON C.USERID = U.USERID WHERE ROOMID = :roomId ORDER BY CREATED_AT ASC";

    Map<String, Object> params = new HashMap<>();
    params.put("roomId", roomId);

    List<Map<String, Object>> resultList = jdbc.queryForList(sql, params);

    List<ChatLog> chatLogList = new ArrayList<ChatLog>();
    for (Map<String, Object> map : resultList) {
      int chatLogId = Integer.parseInt(String.valueOf(map.get("CHATLOGID")));
      String userId = (String) map.get("USERID");
      String userName = (String) map.get("USERNAME");
      String userImgPath = (String) map.get("USERIMGPATH");
      String message = (String) map.get("MESSAGE");
      Object createdAtObj = map.get("CREATED_AT");

      // Timestamp型へ変換
      Timestamp createdAt;
      if (createdAtObj instanceof Timestamp) {
        createdAt = (Timestamp) createdAtObj;
      } else {
        createdAt = Timestamp.valueOf((String) createdAtObj);
      }

      ChatLog chatLog = new ChatLog(
          chatLogId,
          roomId,
          userId,
          userName,
          userImgPath,
          message,
          createdAt);
      chatLogList.add(chatLog);
    }

    return chatLogList;
  }

  /**
   * ルーム情報取得.
   *
   * @param roomId 部屋ID
   * @param userId ユーザID
   * @return ルーム情報
   */
  public Room getRoom(String roomId) {
    final String sqlGetRoom = "SELECT R.ROOMID, R.ROOMNAME, R.CREATEDUSERID, R.DIRECTED, R.PRIVATED, COUNT(*) AS MEMBER_COUNT FROM ROOMS R JOIN JOINROOM J ON R.ROOMID = J.ROOMID WHERE R.ROOMID = :roomId GROUP BY R.ROOMID, R.ROOMNAME, R.DIRECTED";

    Map<String, Object> paramsGetRoom = new HashMap<>();
    paramsGetRoom.put("roomId", roomId);

    List<Map<String, Object>> resultGetRoom = jdbc.queryForList(sqlGetRoom, paramsGetRoom);

    String roomName = "";
    String createdUserId = "";
    boolean directed = false;
    boolean privated = false;
    int memberCount = 0;
    for (Map<String, Object> map : resultGetRoom) {
      roomName = (String) map.get("ROOMNAME");
      createdUserId = (String) map.get("CREATEDUSERID");
      directed = (boolean) map.get("DIRECTED");
      privated = (boolean) map.get("PRIVATED");
      memberCount = Integer.parseInt(String.valueOf(map.get("MEMBER_COUNT")));
    }
    Room room = new Room(roomId, roomName, createdUserId, directed, privated, memberCount);

    return room;
  }

  /**
   * ダイレクト用の追加部屋情報取得.
   *
   * @param room   ルーム情報
   * @param userId ユーザID
   * @return ダイレクト用の追加部屋情報
   */
  public Room getDirect(Room room, String userId) {
    final String sql = "SELECT U.USERNAME AS ROOMNAME FROM JOINROOM R JOIN USERS U ON R.USERID = U.USERID WHERE R.USERID <> :userId AND ROOMID = :roomId";

    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("roomId", room.roomId());

    List<Map<String, Object>> resultGetDirectRoom = jdbc.queryForList(sql, params);

    String roomName = "";
    for (Map<String, Object> map : resultGetDirectRoom) {
      roomName = (String) map.get("ROOMNAME");
    }

    Room direct = new Room(
        room.roomId(),
        roomName,
        room.createdUserId(),
        true,
        true,
        2 // 2人で固定
    );

    return direct;
  }

  /**
   * ユーザが紐づくルーム情報リスト取得.
   *
   * @param userId ユーザID
   * @return ルーム情報リスト
   */
  public ArrayList<Room> getRoomList(String userId) {
    final String sql = "SELECT R.ROOMID, R.ROOMNAME, R.CREATEDUSERID, R.DIRECTED, R.PRIVATED FROM JOINROOM J JOIN ROOMS R ON J.ROOMID = R.ROOMID WHERE J.USERID = :userId AND R.DIRECTED = FALSE ORDER BY R.ROOMNAME ASC";

    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);

    List<Map<String, Object>> resultList = jdbc.queryForList(sql, params);

    ArrayList<Room> roomlist = new ArrayList<Room>();
    for (Map<String, Object> map : resultList) {
      String roomId = (String) map.get("ROOMID");
      String roomName = (String) map.get("ROOMNAME");
      String createdUserId = (String) map.get("CREATEDUSERID");
      boolean directed = (boolean) map.get("DIRECTED");
      boolean privated = (boolean) map.get("PRIVATED");
      roomlist.add(new Room(roomId, roomName, createdUserId, directed, privated, 0));
    }

    return roomlist;
  }

  /**
   * ユーザが紐づくダイレクトルーム情報リスト取得.
   *
   * @param userId ユーザID
   * @return ダイレクトルーム情報リスト
   */
  public ArrayList<Room> getDirectList(String userId) {
    final String sql = "SELECT R.ROOMID, U.USERNAME AS ROOMNAME FROM JOINROOM R JOIN USERS U ON R.USERID = U.USERID WHERE R.USERID <> :userId1 AND ROOMID IN (SELECT R.ROOMID FROM JOINROOM J JOIN ROOMS R ON J.ROOMID = R.ROOMID WHERE J.USERID = :userId2 AND R.DIRECTED = TRUE) ORDER BY R.USERID";

    Map<String, Object> params = new HashMap<>();
    params.put("userId1", userId);
    params.put("userId2", userId);

    List<Map<String, Object>> resultList = jdbc.queryForList(sql, params);

    ArrayList<Room> roomlist = new ArrayList<Room>();
    for (Map<String, Object> map : resultList) {
      String roomId = (String) map.get("ROOMID");
      String roomName = (String) map.get("ROOMNAME");
      roomlist.add(new Room(roomId, roomName));
    }

    return roomlist;
  }

  /**
   * チャットログ保存.
   *
   * @param roomId  部屋ID
   * @param userId  ユーザID
   * @param message メッセージ
   * @return 保存結果
   */
  public boolean saveChatlog(String roomId, String userId, String message) {
    final String sql = "INSERT INTO CHATLOG (CHATLOGID, ROOMID, USERID, MESSAGE, CREATED_AT) VALUES (nextval('CHATLOGID_SEQ'), :roomId, :userId, :message, CURRENT_TIMESTAMP)";

    Map<String, Object> params = new HashMap<>();
    params.put("roomId", roomId);
    params.put("userId", userId);
    params.put("message", message);

    int row = jdbc.update(sql, params);

    if (row != AppConstants.EXPECTED_UPDATE_COUNT) {
      // 更新件数が異常な場合
      throw new IncorrectResultSizeDataAccessException(
          "更新に失敗しました",
          AppConstants.EXPECTED_UPDATE_COUNT);
    }

    return true;
  }

}
