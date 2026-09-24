package com.swack.hcs.repository;

import com.swack.hcs.bean.Room;
import com.swack.hcs.bean.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * ルーム機能に関するDBアクセスを行う.
 */
@Repository
public class RoomRepository {

  @Autowired
  private NamedParameterJdbcTemplate jdbc;

  /**
   * 指定された部屋IDに基づいた部屋情報取得.
   *
   * @param roomId 部屋ID
   * @return 部屋情報
   */
  public Room getRoom(String roomId) {
    final String sql = "SELECT ROOMNAME, CREATEDUSERID, DIRECTED, PRIVATED FROM ROOMS WHERE ROOMID = :roomId";

    Map<String, Object> params = new HashMap<>();
    params.put("roomId", roomId);

    List<Map<String, Object>> resultList = jdbc.queryForList(sql, params);

    Room room = null;
    for (Map<String, Object> map : resultList) {
      String roomName = (String) map.get("ROOMNAME");
      String createdUserId = (String) map.get("CREATEDUSERID");
      boolean directed = (boolean) map.get("DIRECTED");
      boolean privated = (boolean) map.get("PRIVATED");
      room = new Room(roomId, roomName, createdUserId, directed, privated, 0);
    }

    return room;
  }

  /**
   * ルームを新規登録する.
   *
   * @param room 登録する部屋情報
   */
  public void insertRoom(Room room) {
    final String sql = "INSERT INTO ROOMS (ROOMID, ROOMNAME, CREATEDUSERID, DIRECTED, PRIVATED) "
        + "VALUES (:roomId, :roomName, :createdUserId, :directed, :privated)";

    Map<String, Object> params = new HashMap<>();
    params.put("roomId", room.roomId());
    params.put("roomName", room.roomName());
    params.put("createdUserId", room.createdUserId());
    params.put("directed", room.directed());
    params.put("privated", room.privated());

    jdbc.update(sql, params);
  }

  /**
   * 指定されたルーム名が既に登録されているか確認する.
   *
   * @param roomName ルーム名
   * @return 登録済みの場合はtrue
   */
  public boolean existsByRoomName(String roomName) {
    final String sql = "SELECT COUNT(*) FROM ROOMS WHERE ROOMNAME = :roomName";

    Map<String, Object> params = new HashMap<>();
    params.put("roomName", roomName);

    Integer count = jdbc.queryForObject(sql, params, Integer.class);
    return count != null && count > 0;
  }

  /**
   * 現在登録されている部屋の中で、最も大きい数値のROOMIDを取得する.
   *
   * @return 現在の最大ROOMID（1件も存在しない場合はnull）
   */
  public String getMaxRoomId() {
    final String sql = "SELECT MAX(ROOMID) AS MAXROOMID FROM ROOMS";
    return jdbc.queryForObject(sql, new HashMap<>(), String.class);
  }

  /**
   * ルーム作成時に、作成者を最初のメンバーとしてJOINROOMへ登録する.
   *
   * @param roomId 部屋ID
   * @param userId 参加するユーザID
   */
  public void insertJoinRoom(String roomId, String userId) {
    final String sql = "INSERT INTO JOINROOM (ROOMID, USERID) VALUES (:roomId, :userId)";

    Map<String, Object> params = new HashMap<>();
    params.put("roomId", roomId);
    params.put("userId", userId);

    jdbc.update(sql, params);
  }

  /**
   * 参加可能な公開ルーム一覧を取得する（自分が参加済みのものは除く）.
   *
   * @param userId ユーザID
   * @return 参加可能な公開ルームのリスト
   */
  public List<Room> getPublicRooms(String userId) {
    final String sql = "SELECT ROOMID, ROOMNAME FROM ROOMS "
        + "WHERE PRIVATED = FALSE "
        + "AND ROOMID NOT IN (SELECT ROOMID FROM JOINROOM WHERE USERID = :userId)";

    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);

    List<Map<String, Object>> resultList = jdbc.queryForList(sql, params);

    List<Room> roomList = new ArrayList<>();
    for (Map<String, Object> map : resultList) {
      String roomId = (String) map.get("ROOMID");
      String roomName = (String) map.get("ROOMNAME");
      roomList.add(new Room(roomId, roomName));
    }

    return roomList;
  }

  /**
   * 指定されたユーザーが指定されたルームに既に参加しているかを確認する.
   *
   * @param roomId ルームID
   * @param userId ユーザID
   * @return 参加済みの場合はtrue
   */
  public boolean isRoomJoined(String roomId, String userId) {
    final String sql = "SELECT COUNT(*) FROM JOINROOM WHERE ROOMID = :roomId AND USERID = :userId";

    Map<String, Object> params = new HashMap<>();
    params.put("roomId", roomId);
    params.put("userId", userId);

    Integer count = jdbc.queryForObject(sql, params, Integer.class);
    return count != null && count > 0;
  }

  /**
   * 指定されたルーム名に基づいたルームIDを取得する.
   *
   * @param roomName ルーム名
   * @return ルームID
   */
  public String getRoomIdByName(String roomName) {
    final String sql = "SELECT ROOMID FROM ROOMS WHERE ROOMNAME = :roomName";

    Map<String, Object> params = new HashMap<>();
    params.put("roomName", roomName);

    return jdbc.queryForObject(sql, params, String.class);
  }

  /**
   * 指定されたルームIDに基づいたルーム名を取得する.
   */
  public List<Room> getUserRooms(String userId) {
    final String sql = "SELECT ROOMID, ROOMNAME FROM ROOMS WHERE ROOMID IN (SELECT ROOMID FROM JOINROOM WHERE USERID = :userId)";

    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);

    List<Map<String, Object>> resultList = jdbc.queryForList(sql, params);

    List<Room> roomList = new ArrayList<>();
    for (Map<String, Object> map : resultList) {
      String roomId = (String) map.get("ROOMID");
      String roomName = (String) map.get("ROOMNAME");
      roomList.add(new Room(roomId, roomName));
    }

    return roomList;
  }

  /**
   * 指定されたユーザーが参加している直接チャットルームの一覧を取得する.
   * @param userId
   * @return
   */
  public List<Room> getDirectRooms(String userId) {
    final String sql = "SELECT R.ROOMID, U.USERNAME AS ROOMNAME FROM JOINROOM R JOIN USERS U ON R.USERID = U.USERID WHERE R.USERID <> :userId1 AND ROOMID IN (SELECT R.ROOMID FROM JOINROOM J JOIN ROOMS R ON J.ROOMID = R.ROOMID WHERE J.USERID = :userId2 AND R.DIRECTED = TRUE) ORDER BY R.USERID";

    Map<String, Object> params = new HashMap<>();
    params.put("userId1", userId);
    params.put("userId2", userId);

    List<Map<String, Object>> resultList = jdbc.queryForList(sql, params);

    List<Room> roomList = new ArrayList<>();
    for (Map<String, Object> map : resultList) {
      String roomId = (String) map.get("ROOMID");
      String roomName = (String) map.get("ROOMNAME");
      roomList.add(new Room(roomId, roomName));
    }

    return roomList;
  }

  /**
   * 指定されたルームに参加可能なユーザーの一覧を取得する.
   * @param roomId
   * @return
   */
  public List<UserData> getUsers(String userId) {
    final String sql = "SELECT USERID, USERNAME FROM USERS WHERE USERID <> :userId";

    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);

    List<Map<String, Object>> resultList = jdbc.queryForList(sql, params);

    List<UserData> userList = new ArrayList<>();
    for (Map<String, Object> map : resultList) {
      String targetuserId = (String) map.get("USERID");
      String userName = (String) map.get("USERNAME");
      userList.add(new UserData(targetuserId, userName));
    }

    return userList;
  }

  /**
   * 指定されたルームに参加可能なユーザーの一覧を取得する.
   * @param roomId
   * @return
   */
  public List<UserData> getJoinableUsers(String roomId) {
    final String sql = "SELECT USERID, USERNAME FROM USERS WHERE USERID NOT IN (SELECT USERID FROM JOINROOM WHERE ROOMID = :roomId)";

    Map<String, Object> params = new HashMap<>();
    params.put("roomId", roomId);

    List<Map<String, Object>> resultList = jdbc.queryForList(sql, params);

    List<UserData> userList = new ArrayList<>();
    for (Map<String, Object> map : resultList) {
      String targetuserId = (String) map.get("USERID");
      String userName = (String) map.get("USERNAME");
      userList.add(new UserData(targetuserId, userName));
    }

    return userList;
  }

  /**
   * 指定されたダイレクトルームに参加可能なユーザーの一覧を取得する.
   * @param userId
   * @return まだダイレクトルームに参加していないユーザーの一覧
   */
  public List<UserData> getDirectUsers(String userId) {
    final String SQL = "SELECT USERID, USERNAME FROM USERS WHERE USERID <> :userId AND USERID NOT IN (SELECT USERID FROM JOINROOM J1 WHERE J1.ROOMID IN (SELECT J2.ROOMID FROM JOINROOM J2 JOIN ROOMS R ON J2.ROOMID = R.ROOMID WHERE J2.USERID = :userId AND R.DIRECTED = TRUE) AND J1.USERID <> :userId)";

    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);

    List<Map<String, Object>> resultList = jdbc.queryForList(SQL, params);
    List<UserData> userList = new ArrayList<>();
    for (Map<String, Object> map : resultList) {
      String targetUserId = (String) map.get("USERID");
      String userName = (String) map.get("USERNAME");
      userList.add(new UserData(targetUserId, userName));
    }
    return userList;
  }
}
