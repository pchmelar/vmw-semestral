//
//  SAResultDetailViewController.swift
//  SurfApp
//
//  Created by filletzz on 12/12/15.
//  Copyright © 2015 filletzz. All rights reserved.
//

import UIKit
import SnapKit

class SAResultDetailViewController: UIViewController {
  
  var image:UIImage
  
  init(image:UIImage) {
    self.image = image
    super.init(nibName: nil, bundle: nil)
  }
  
  required init?(coder aDecoder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    // Do any additional setup after loading the view.
    
    self.title = "Detail"
    self.view.backgroundColor = UIColor.blackColor()
    
    //load image
    let imageView = UIImageView.init(image: self.image)
    imageView.contentMode = .ScaleAspectFit
    
    self.view.addSubview(imageView)
    imageView.snp_makeConstraints { (make) -> Void in
      make.top.equalTo(self.view.snp_top).offset(UIApplication.sharedApplication().statusBarFrame.size.height + self.navigationController!.navigationBar.frame.size.height)
      make.right.equalTo(self.view.snp_right)
      make.bottom.equalTo(self.view.snp_bottom)
      make.left.equalTo(self.view.snp_left)
    }
  }
  
  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
    // Dispose of any resources that can be recreated.
  }
  
  
  /*
  // MARK: - Navigation
  
  // In a storyboard-based application, you will often want to do a little preparation before navigation
  override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
  // Get the new view controller using segue.destinationViewController.
  // Pass the selected object to the new view controller.
  }
  */
  
}
