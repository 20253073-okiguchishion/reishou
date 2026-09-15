package com.swack.hcs.service;

import com.swack.hcs.bean.ChatLog;
import com.swack.hcs.bean.Room;
import com.swack.hcs.repository.ChatRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * チャット機能を実行するサービスクラス.
 * チャットルームの取得、チャットログの保存、編集、削除の管理などを行います。
 */
@Transactional
@Service
public class ChatService {

  @Autowired
  private ChatRepository chatRepository;

  /**
   * 指定されたルームIDとユーザーIDに基づいたチャットルーム情報取得.
   *
   * @param roomId チャットルームのID
   * @param userId ユーザーのID
   * @return 取得したチャットルームの情報
   */
  public Room getRoom(String roomId, String userId) {
    Room room = chatRepository.getRoom(roomId);

    if (room.directed()) {
      // ダイレクト用の追加部屋情報を取得
      room = chatRepository.getDirect(room, userId);
    }

    return room;
  }

  /**
   * 指定されたユーザーIDに関連するすべてのチャットルームリスト取得.
   *
   * @param userId ユーザーのID
   * @return チャットルームのリスト
   */
  public ArrayList<Room> getRoomList(String userId) {
    return chatRepository.getRoomList(userId);
  }

  /**
   * 指定されたユーザーIDに関連するダイレクトチャットリスト取得.
   *
   * @param userId ユーザーのID
   * @return ダイレクトチャットのリスト
   */
  public ArrayList<Room> getDirectList(String userId) {
    return chatRepository.getDirectList(userId);
  }

  /**
   * 指定されたユーザーIDとルームIDに基づいたチャットログリスト取得.
   *
   * @param userId ユーザーのID
   * @param roomId チャットルームのID
   * @return チャットログのリスト
   */
  public List<ChatLog> getChatlogList(String userId, String roomId) {
    List<ChatLog> chatLogList = chatRepository.getChatlogList(roomId);

    return chatLogList;
  }

  /**
   * チャットログ保存.
   *
   * @param roomId  チャットルームのID
   * @param userId  ユーザーのID
   * @param message メッセージの内容
   */
  public void saveChatLog(String roomId, String userId, String message) {
    chatRepository.saveChatlog(roomId, userId, message);
  }

}
