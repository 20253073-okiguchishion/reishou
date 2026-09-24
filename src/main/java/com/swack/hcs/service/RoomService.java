package com.swack.hcs.service;

import com.swack.hcs.bean.Room;
import com.swack.hcs.bean.UserData;
import com.swack.hcs.repository.RoomRepository;
import java.util.List;
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

  /** 部屋IDの接頭辞. */
  private static final String ROOM_ID_PREFIX = "R";

  /** 部屋IDの連番部分の桁数. */
  private static final int ROOM_ID_SEQ_LENGTH = 4;

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private UserService userService;

  /**
   * 指定されたルームIDに基づいたルーム情報取得.
   *
   * @param roomId ルームID
   * @return 取得したルームの情報
   */
  public Room getRoom(String roomId) {
    return roomRepository.getRoom(roomId);
  }

  /**
   * ルーム名が既に登録済みかどうかを確認する.
   *
   * @param roomName ルーム名
   * @return 登録済みの場合はtrue
   */
  public boolean isRegistered(String roomName) {
    return roomRepository.existsByRoomName(roomName);
  }

  /**
   * 新しいルームを作成する.
   *
   * @param roomName      ルーム名
   * @param privated      非公開ルームかどうか
   * @param createdUserId 作成者のユーザID
   * @return 作成されたルーム情報
   */
  public Room createRoom(String roomName, boolean privated, String createdUserId) {
    String roomId = generateRoomId();
    Room room = new Room(roomId, roomName, createdUserId, false, privated, 1);
    roomRepository.insertRoom(room);
    roomRepository.insertJoinRoom(roomId, createdUserId);
    return room;
  }

  /**
   * 参加可能な公開ルーム一覧を取得する（自分が参加済みのものは除く）.
   *
   * @param userId ユーザID
   * @return 参加可能な公開ルームのリスト
   */
  public List<Room> getPublicRooms(String userId) {
    return roomRepository.getPublicRooms(userId);
  }

  /**
   * 指定されたルームに参加する.
   *
   * @param roomId 参加するルームID
   * @param userId 参加するユーザID
   */
  public void joinPublicRoom(String roomId, String userId) {
    roomRepository.insertJoinRoom(roomId, userId);
  }

  /**
   * 次のルームIDを採番する.
   *
   * @return 採番されたルームID
   */
  private String generateRoomId() {
    String maxRoomId = roomRepository.getMaxRoomId();
    int nextSeq = 1;
    if (maxRoomId != null && maxRoomId.length() == (1 + ROOM_ID_SEQ_LENGTH)) {
      nextSeq = Integer.parseInt(maxRoomId.substring(1)) + 1;
    }
    return String.format("%s%0" + ROOM_ID_SEQ_LENGTH + "d", ROOM_ID_PREFIX, nextSeq);
  }

  /**
   * 指定されたルームにユーザが既に参加しているか確認する.
   *
   * @param roomId ルームID
   * @param userId ユーザID
   * @return 既に参加している場合はtrue
   */
  public boolean isRoomJoined(String roomId, String userId) {
    return roomRepository.isRoomJoined(roomId, userId);
  }

  /**
   * 指定されたルーム名が既に存在するか確認する.
   * @param roomName
   * @return
   */
  public boolean isRoomNameExists(String roomName) {
    return roomRepository.existsByRoomName(roomName);
  }

  /**
   * 指定されたユーザーが参加しているルームの一覧を取得する.
   * @param userId
   * @return
   */
  public List<Room> getUserRooms(String userId) {
    return roomRepository.getUserRooms(userId);
  }

  /**
   * 指定されたユーザーが参加している直接チャットルームの一覧を取得する.
   * @param userId
   * @return
   */
  public List<Room> getDirectRooms(String userId) {
    return roomRepository.getDirectRooms(userId);
  }

  /**
   * 指定されたルームにユーザーを招待する.
   * @param roomId
   * @param userIds
   */
  public void inviteUsers(String roomId, List<String> userIds) {
    for (String userId : userIds) {
      roomRepository.insertJoinRoom(roomId, userId);
    }
  }

  /**
   * 指定されたユーザーの一覧を取得する.
   * @param userId
   * @return
   */
  public List<UserData> getUsers(String userId) {
    return roomRepository.getUsers(userId);
  }

  /**
   * 指定されたルームに参加可能なユーザーの一覧を取得する.
   * @param roomId
   * @return
   */
  public List<UserData> getJoinableUsers(String roomId) {
    return roomRepository.getJoinableUsers(roomId);
  }

  /**
   * ダイレクトメッセージ用のルームを作成する.
   * @param createdUserId 作成者のユーザーID
   * @param targetUserId 招待するユーザーのID
   * @return 作成されたルーム情報
   */
  public Room createDirectRoom(String createdUserId, String targetUserId) {
    String roomId = generateRoomId();
    String roomName = userService.getUserName(targetUserId); // ルーム名を相手のユーザー名に設定
    Room room = new Room(roomId, roomName, createdUserId, true, true, 2);
    roomRepository.insertRoom(room);
    roomRepository.insertJoinRoom(roomId, createdUserId);
    roomRepository.insertJoinRoom(roomId, targetUserId);
    return room;
  }

  public List<UserData> getDirectUsers(String userId) {
    return roomRepository.getDirectUsers(userId);
  }
}
