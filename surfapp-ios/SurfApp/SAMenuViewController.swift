//
//  SAMenuViewController.swift
//  SurfApp
//
//  Created by filletzz on 16/11/15.
//  Copyright © 2015 filletzz. All rights reserved.
//

import UIKit
import SnapKit
import Alamofire
import SwiftyJSON
import SVProgressHUD

class SAMenuViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
  
  let imagePicker = UIImagePickerController()
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    // Do any additional setup after loading the view.
    
    self.title = "SurfApp"
    
    //set imagePicker
    imagePicker.delegate = self
    imagePicker.allowsEditing = false
    
    //add background
    let bg = UIImageView()
    bg.image = UIImage(named: "bg.png")
    bg.contentMode = .ScaleAspectFill
    self.view.addSubview(bg)
    bg.snp_makeConstraints { (make) -> Void in
      make.edges.equalTo(self.view)
    }
    
    //add libraryButton
    let libraryButton = UIButton(type: UIButtonType.System) as UIButton
    libraryButton.backgroundColor = UIColor.whiteColor()
    libraryButton.layer.cornerRadius = 5
    libraryButton.layer.masksToBounds = true
    libraryButton.setTitle("Choose from library", forState: UIControlState.Normal)
    libraryButton.addTarget(self, action: "libraryButtonAction:", forControlEvents: UIControlEvents.TouchUpInside)
    self.view.addSubview(libraryButton)
    libraryButton.snp_makeConstraints { (make) -> Void in
      make.right.equalTo(self.view.snp_right).offset(-20)
      make.bottom.equalTo(self.view.snp_bottom).offset(-50)
      make.left.equalTo(self.view.snp_left).offset(20)
      make.height.equalTo(50)
    }
    
    //add cameraButton
    let cameraButton = UIButton(type: UIButtonType.System) as UIButton
    cameraButton.backgroundColor = UIColor.whiteColor()
    cameraButton.layer.cornerRadius = 5
    cameraButton.layer.masksToBounds = true
    cameraButton.setTitle("Take photo", forState: UIControlState.Normal)
    cameraButton.addTarget(self, action: "cameraButtonAction:", forControlEvents: UIControlEvents.TouchUpInside)
    self.view.addSubview(cameraButton)
    cameraButton.snp_makeConstraints { (make) -> Void in
      make.right.equalTo(self.view.snp_right).offset(-20)
      make.bottom.equalTo(libraryButton.snp_top).offset(-10)
      make.left.equalTo(self.view.snp_left).offset(20)
      make.height.equalTo(50)
    }
    
    //add image
    let logo = UIImageView()
    logo.image = UIImage(named: "logo.png")
    logo.contentMode = .ScaleAspectFit
    self.view.addSubview(logo)
    logo.snp_makeConstraints { (make) -> Void in
      make.top.equalTo(self.view.snp_top).offset(80)
      make.right.equalTo(self.view.snp_right).offset(-20)
      make.bottom.equalTo(cameraButton.snp_top).offset(-20)
      make.left.equalTo(self.view.snp_left).offset(20)
    }
    
  }
  
  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
    // Dispose of any resources that can be recreated.
  }
  
  func cameraButtonAction(sender:UIButton!) {
    imagePicker.sourceType = .Camera
    self.presentViewController(imagePicker, animated: true, completion: nil)
  }
  
  func libraryButtonAction(sender:UIButton!) {
    imagePicker.sourceType = .PhotoLibrary
    self.presentViewController(imagePicker, animated: true, completion: nil)
  }
  
  
  /*
  // MARK: - Navigation
  
  // In a storyboard-based application, you will often want to do a little preparation before navigation
  override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
  // Get the new view controller using segue.destinationViewController.
  // Pass the selected object to the new view controller.
  }
  */
  
  // MARK: - UIImagePickerController
  
  func imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : AnyObject]){
    
    //convert img to base64
    let selectedImage = info[UIImagePickerControllerOriginalImage] as? UIImage
    let imageData = UIImageJPEGRepresentation(selectedImage!, 0.3)
    let base64String = imageData!.base64EncodedStringWithOptions(.EncodingEndLineWithCarriageReturn)

    //prepare POST
    let headers = [
      "Content-Type": "application/json;charset=UTF-8"
    ]
    let data = [
      "data": "\(base64String)"
    ]
    
    //dismiss imagePicker
    SVProgressHUD.showWithStatus("Loading results", maskType: SVProgressHUDMaskType.Clear)
    self.imagePicker.dismissViewControllerAnimated(true, completion: nil)
    
    //send img to api
    Alamofire.request(.POST, "https://tdbs.target-md.com:8443/surfapp/api/photo/upload64", headers: headers, parameters: data, encoding: .JSON)
      .responseJSON { response in
        
        switch response.result {
          
        case .Success:
          if let value = response.result.value {
            let json = JSON(value)
            print("JSON: \(json)")
            
            //push results view controller
            let resultsViewController: SAResultsViewController = SAResultsViewController(photo: selectedImage!, results: json)
            self.navigationController!.pushViewController(resultsViewController, animated: true)
          }
          
        case .Failure(let error):
          print(error)
          SVProgressHUD.showErrorWithStatus("Network error\nCan't send photo")
        }
        
      }
    
  }
  
}
