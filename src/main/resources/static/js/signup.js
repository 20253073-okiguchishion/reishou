"use strict";
document.addEventListener("DOMContentLoaded", () => {
  // HTML要素を変数に代入
  const elSendButton = document.getElementById("send");
  const elName = document.getElementById("name");
  const elMailAddress = document.getElementById("mailAddress");
  const elPassword = document.getElementById("password");

  elName.addEventListener("keyup", () => {
    if (
      elName.value.length === 0 ||
      elMailAddress.value.length === 0 ||
      elPassword.value.length === 0
    ) {
      // 空の場合：primaryを消して、secondary（グレー）にする
      elSendButton.classList.remove("btn-primary");
      elSendButton.classList.add("btn-secondary");
      elSendButton.disabled = true;
    } else {
      // 入力あり：secondaryを消して、primary（青）に戻す
      elSendButton.classList.remove("btn-secondary");
      elSendButton.classList.add("btn-primary");
      elSendButton.disabled = false;
    }
  });

  elMailAddress.addEventListener("keyup", () => {
    if (
      elMailAddress.value.length === 0 ||
      elName.value.length === 0 ||
      elPassword.value.length === 0
    ) {
      // 空の場合：primaryを消して、secondary（グレー）にする
      elSendButton.classList.remove("btn-primary");
      elSendButton.classList.add("btn-secondary");
      elSendButton.disabled = true;
    } else {
      // メッセージ入力欄にテキストがある場合、送信ボタンを有効化
      // 入力あり：secondaryを消して、primary（青）に戻す
      elSendButton.classList.remove("btn-secondary");
      elSendButton.classList.add("btn-primary");
      elSendButton.disabled = false;
    }
  });

  elPassword.addEventListener("keyup", () => {
    if (
      elPassword.value.length === 0 ||
      elName.value.length === 0 ||
      elMailAddress.value.length === 0
    ) {
      // 空の場合：primaryを消して、secondary（グレー）にする
      elSendButton.classList.remove("btn-primary");
      elSendButton.classList.add("btn-secondary");
      elSendButton.disabled = true;
    } else {
      // メッセージ入力欄にテキストがある場合、送信ボタンを有効化
      // 入力あり：secondaryを消して、primary（青）に戻す
      elSendButton.classList.remove("btn-secondary");
      elSendButton.classList.add("btn-primary");
      elSendButton.disabled = false;
    }
  });
});

function pushHideButton() {
  var txtPass = document.getElementById("password");
  var btnEye = document.getElementById("buttonEye");
  if (txtPass.type === "text") {
    txtPass.type = "password";
    btnEye.className = "fa fa-eye";
  } else {
    txtPass.type = "text";
    btnEye.className = "fa fa-eye-slash";
  }
}
