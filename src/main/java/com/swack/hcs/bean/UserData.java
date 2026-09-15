package com.swack.hcs.bean;

/**
 * ユーザ情報を管理するBean.
 * ユーザーの基本情報、権限、プロフィール画像などを保持します。
 */
public record UserData(
    /**
     * ユーザID.
     * ユーザーを一意に識別するためのIDです。
     */
    String userId,
    /**
     * ユーザ名.
     * ユーザーの名前です。
     */
    String userName,
    /**
     * メールアドレス.
     * ユーザーのメールアドレスです。
     */
    String mailAddress,
    /**
     * パスワード.
     * ユーザーのログインパスワードです。
     */
    String password,
    /**
     * ユーザ画像パス.
     * ユーザーのプロフィール画像のパスです。
     * 画像がない場合は空文字列です。
     */
    String userImgPath,
    /**
     * 権限.
     * ユーザーのシステム上の権限（例：ADMIN、USERなど）を保持します。
     */
    String role) {

  /** 管理者権限判定用文字列 */
  private static final String ROLE_ADMIN_KEY = "ADMIN";

  /**
   * 管理者権限を持っているかチェック.
   *
   * @return 管理者権限を持っている場合はtrue、持っていない場合はfalse
   */
  public boolean isAdmin() {
    if (this.role().equals(ROLE_ADMIN_KEY)) {
      return true;
    } else {
      return false;
    }
  }

}
