module HomeHelper
  def link_to_with_active_class(action, humanized = nil, active_class = nil)
    link_url = {:action => action}
    html_options = current_page?(link_url) ? {:class => active_class || :active} : {}
    content_tag(:li, link_to(humanized || action.humanize, link_url), html_options)
  end
end
