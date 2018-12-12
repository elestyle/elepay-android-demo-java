## [English version](https://github.com/elestyle/elepayAndroidDemoKotlin/blob/master/README.en.md)

# elepay android デモアプリケーション

elepay Android SDK の Java でご利用のデモアプリケーションです。

## 必要な環境

* Android API 21
* Android Studio 3.2.1 +
* Kotlin 1.3 +
* Gradle 4.6 +

> IDEとビルドツールのバージョンは常に更新するので、最新バージョンをご利用するのはおすすめです。

## 利用方法

git clone または zip をダウンロードし、解凍したプロジェクトを Android Studio でオープンします。
ビルドしたアプリを、デバイスまたは Android Emulater で起動させます。

アプリには二つのデモ商品があります。```Pay``` ボタンを押したら、支払い方法の画面が表示されます。
デモのために、一つの商品に全ての支払い方法を使えることではありません。
以下のルールをご参照ください。

* 単価1円のデモ商品は Alipay または UnionPay で払えます。
* 単価100円のデモ商品は クレジットカード または PayPal で払えます。
