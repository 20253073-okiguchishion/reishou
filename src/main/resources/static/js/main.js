"use strict";

// ドキュメントが完全に読み込まれた後に実行されるイベントリスナー
document.addEventListener("DOMContentLoaded", () => {
  // HTML要素を変数に代入
  const elSendButton = document.getElementById("send");
  const elMessage = document.getElementById("message");
  const elMessageForm = document.getElementById("messageForm");
  const elLogArea = document.getElementById("logArea");

  // ログ表示エリアをページの最下部にスクロール
  elLogArea.scrollTop = elLogArea.scrollHeight + 110;

  // メッセージ入力欄でキーボード操作が行われたときのイベントリスナー
  elMessage.addEventListener("keydown", (e) => {
    if (e.ctrlKey && e.key === "Enter" && e.target.value) {
      // Ctrl + Enterが押された場合、フォームを送信
      elMessageForm.submit();
      e.preventDefault(); // デフォルトのイベントをキャンセル
    } else if (e.key === "Enter") {
      // Enterキーだけが押された場合は、デフォルトの送信動作をキャンセル
      e.preventDefault();
    }
  });

  // 送信ボタンの状態を更新する関数
  function updateSendButtonState() {
    if (elMessage.value.length === 0) {
      // メッセージ入力欄が空の場合、送信ボタンを無効化
      elSendButton.style.color = "rgba(44, 45, 48, 0.75)";
      elSendButton.style.backgroundColor = "#e8e8e8";
      elSendButton.disabled = true;
    } else {
      // メッセージ入力欄にテキストがある場合、送信ボタンを有効化
      elSendButton.style.color = "#ffffff";
      elSendButton.style.backgroundColor = "#008952";
      elSendButton.disabled = false;
    }
  }

  // ページの初期状態設定
  updateSendButtonState();

  // メッセージ入力欄の内容が変更されたときのイベントリスナー
  elMessage.addEventListener("input", () => {
    updateSendButtonState();
  });
});

// ログアウト処理を行う関数
function logout() {
  const elLogoutForm = document.getElementById("logoutForm");

  elLogoutForm.submit();
}
