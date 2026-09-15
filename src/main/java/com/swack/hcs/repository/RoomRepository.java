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

}
