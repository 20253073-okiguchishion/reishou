package com.swack.hcs.service;

import com.swack.hcs.bean.Room;
import com.swack.hcs.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ルーム機能を実行するサービスクラス.
 * ルームの作成、参加、ユーザーの取得などの機能を提供します。
 */
@Transactional
@Service
public class RoomService {

  @Autowired
  private RoomRepository roomRepository;

  /**
   * 指定されたルームIDに基づいたルーム情報取得.
   *
   * @param roomId ルームID
   * @return 取得したルームの情報
   */
  public Room getRoom(String roomId) {
    return roomRepository.getRoom(roomId);
  }

}
