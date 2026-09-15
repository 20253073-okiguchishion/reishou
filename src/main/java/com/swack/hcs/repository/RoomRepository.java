package com.swack.hcs.repository;

import com.swack.hcs.bean.Room;
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
   * 部屋IDは "R" + 4桁の連番（例: R0001）という体系で払い出す.
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

}
