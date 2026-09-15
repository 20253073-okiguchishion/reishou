package com.swack.hcs.bean;

import java.sql.Timestamp;

/**
 * チャットログ情報を管理するBean.
 * チャットの各メッセージに関する詳細情報を保持します。
 */
public record ChatLog(
    /**
     * チャットログID.
     * 各チャットメッセージを一意に識別します。
     */
    int chatLogId,
    /**
     * ルームID.
     * このチャットメッセージが属するチャットルームを識別します。
     */
    String roomId,
    /**
     * ユーザID.
     * メッセージの投稿者を識別します。
     */
    String userId,
    /**
     * ユーザ名.
     * メッセージの投稿者の名前です。
     */
    String userName,
    /**
     * ユーザ画像パス.
     * メッセージの投稿者のプロフィール画像のパスを格納します。
     * 画像がない場合は空文字列です。
     */
    String userImgPath,
    /**
     * メッセージ.
     * チャットでのメッセージ内容です。
     */
    String message,
    /**
     * 投稿日時.
     * メッセージが投稿された時刻です。
     */
    Timestamp createdAt) {
}
