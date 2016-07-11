post '/authorize', forward: '/auth/exchange-token.groovy'

post '/webhook', forward: '/github-webhook/receive-event.groovy'
post '/webhook_test', forward: '/github-webhook/receive-test.groovy'

get '/@owner/@repo/status', forward: '/status.groovy?full_name=@owner/@repo'

get '/@owner/@repo/status.svg', forward: '/badge/gradle-status.groovy?full_name=@owner/@repo'
