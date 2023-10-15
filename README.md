# システム概要
1. データ収集
- YouTube API: 日々の急上昇動画ランキングを取得します。
- Google Cloud Functions: データを取得してBigQueryにアップロードするスクリプトを作成する。
- Cloud Scheduler: データ取得とBigQueryにアップロードする処理を定期的に自動実行する。
2. データウェアハウス
- BigQuery: スクリプトから送信されたデータを保存するためのデータウェアハウスとしてBigQueryを使用する。
3. データ可視化
- Data Studio BigQueryと直接統合されているGoogleのデータ可視化ツール。

# システム設計図
<img width="805" alt="スクリーンショット 2023-10-15 20 43 12" src="https://github.com/taca10/youtube-data-pipeline/assets/25317424/676d8c8c-fc5a-4df3-9c3b-1d666a34b833">
