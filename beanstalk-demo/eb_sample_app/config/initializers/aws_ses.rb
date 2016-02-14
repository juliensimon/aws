require 'net/smtp'

module Net
  class SMTP
    def tls?
      true
    end
  end
end