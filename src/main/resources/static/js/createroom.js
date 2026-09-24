"use strict";
document.addEventListener("DOMContentLoaded", () => {
  // HTML要素を変数に代入
  const elSendButton = document.getElementById("send");
  const elName = document.getElementById("name");
  const elCheckBox = document.getElementById("chk");

  elCheckBox.addEventListener("change", (e) => {
    if (elCheckBox.checked) {
      document.getElementById("pageTitle").textContent = "プライベートルームを作成する";
      document.getElementById("check_label").textContent = "プライベート";
      document.getElementById("check_text").textContent =
        "このルームは、招待によってのみ参加または確認することができます。";
    } else {
      document.getElementById("pageTitle").textContent = "ルームを作成する";
      document.getElementById("check_label").textContent = "パブリック";
      document.getElementById("check_text").textContent =
        "このルームは、ワークスペースのメンバーであれば誰でも閲覧・参加することができます。";
    }
  });

  elName.addEventListener("input", () => {
    if (elName.value.length === 0) {
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
});

$(function () {
  $("select").multipleSelect({
    width: 200,
    formatSelectAll: function () {
      return "すべて";
    },
    formatAllSelected: function () {
      return "全て選択されています";
    },
  });
});
