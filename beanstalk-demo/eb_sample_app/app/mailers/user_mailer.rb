class UserMailer < ActionMailer::Base
  default from: "abhiksingh@gmail.com"  

  def welcome_email(user)
  	@user = user
    begin
      mail(:to => @user.email, :subject => "Thank you!")
    rescue Exception => e
      Rails.logger.warn "Error: mail => #{e.message}"
    end
  end
end
