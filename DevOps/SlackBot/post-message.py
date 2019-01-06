from slacker import Slacker
import os

token = os.environ['SLACK_TOKEN']
slack = Slacker(token)

attachments_dict = dict()
attachments_dict['fallback'] = "이건 노티피케이션에만 나타나는 텍스트지"
attachments_dict['pretext'] = "안녕 날 소개하지 이름은 펭귄봇 용도 테스트"
attachments_dict['title'] = "슬랙봇을 만들고 있는 중이지"
attachments_dict['title_link'] = "https://yenarue.github.io"
attachments_dict['text'] = "본문을 적는 곳이지. 5줄이 넘어가면 *show more*로 보이게 되지. " \
                           "\n넘겨볼까" \
                           "\n넘겨넘겨" \
                           "\n아자아자" \
                           "\n펭귄을 보호합시다" \
                           "\n펭귄 귀여워"
attachments_dict['mrkdwn_in'] = ["text", "pretext"]  # 마크다운을 적용시킬 인자들을 선택한다.
attachments = [attachments_dict]

slack.chat.post_message(channel="#dev-slack", text=None, attachments=attachments, as_user=True)