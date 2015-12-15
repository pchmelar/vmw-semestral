//
//  SAResultsViewController.swift
//  SurfApp
//
//  Created by filletzz on 12/12/15.
//  Copyright © 2015 filletzz. All rights reserved.
//

import UIKit
import SnapKit
import SwiftyJSON
import SVProgressHUD
import MWPhotoBrowser

class SAResultsViewController: UIViewController, UICollectionViewDelegateFlowLayout, UICollectionViewDataSource, MWPhotoBrowserDelegate {
  
  var photo:UIImage
  var results:JSON
  var MWphotos:[AnyObject]
  var collectionView: UICollectionView!
  
  init(photo:UIImage, results:JSON) {
    self.photo = photo
    self.results = results
    self.MWphotos = [AnyObject]()
    super.init(nibName: nil, bundle: nil)
  }
  
  required init?(coder aDecoder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    // Do any additional setup after loading the view.
    
    SVProgressHUD.dismiss()
    
    self.title = "Results"
    self.view.backgroundColor = UIColor.whiteColor()
    
    //show previously taken photo
    let imageView = UIImageView.init(image: self.photo)
    imageView.contentMode = .ScaleToFill
    
    let imageViewRecognizer: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: "photoTapped:")
    imageView.addGestureRecognizer(imageViewRecognizer)
    imageView.userInteractionEnabled = true
    
    self.view.addSubview(imageView)
    imageView.snp_makeConstraints { (make) -> Void in
      make.top.equalTo(self.view.snp_top).offset(UIApplication.sharedApplication().statusBarFrame.size.height + self.navigationController!.navigationBar.frame.size.height + 10)
      make.left.equalTo(self.view.snp_left).offset(10)
      make.width.equalTo((self.view.frame.width-20)/3)
      make.height.equalTo((self.view.frame.width-20)/3)
    }
    
    //set collectionView layout as grid without spacing
    let layout: UICollectionViewFlowLayout = UICollectionViewFlowLayout()
    layout.sectionInset = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
    layout.itemSize = CGSize(width: (self.view.frame.width-20)/3, height: (self.view.frame.width-20)/3)
    layout.minimumInteritemSpacing = 0
    layout.minimumLineSpacing = 0
    
    //init collectionView and add it below the taken photo
    collectionView = UICollectionView(frame: self.view.frame, collectionViewLayout: layout)
    collectionView.dataSource = self
    collectionView.delegate = self
    collectionView.registerClass(UICollectionViewCell.self, forCellWithReuseIdentifier: "Cell")
    collectionView.backgroundColor = UIColor.whiteColor()
    
    self.view.addSubview(collectionView)
    collectionView.snp_makeConstraints { (make) -> Void in
      make.top.equalTo(imageView.snp_bottom).offset(10)
      make.right.equalTo(self.view.snp_right).offset(-10)
      make.bottom.equalTo(self.view.snp_bottom)
      make.left.equalTo(self.view.snp_left).offset(10)
    }
    
    //add photos to MWPhotoBrowser
    MWphotos.append(MWPhoto(image: self.photo))
    for i in 0..<9 {
      MWphotos.append(MWPhoto(URL: NSURL(string: "https://tdbs.target-md.com:8443/surfapp/api/photo/show/id/\(results[i]["id"].intValue)")))
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
  
  // MARK: - UICollectionViewDataSource
  
  func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
    return 9
  }
  
  func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
    
    //init cell
    let cell = collectionView.dequeueReusableCellWithReuseIdentifier("Cell", forIndexPath: indexPath)
    
    //load image
    let imageView = UIImageView.init(frame: CGRectMake(0, 0, (self.view.frame.width-20)/3, (self.view.frame.width-20)/3))
    let url = NSURL(string: "https://tdbs.target-md.com:8443/surfapp/api/photo/show/id/\(results[indexPath.row]["id"].intValue)")
    imageView.image = UIImage(data: NSData(contentsOfURL: url!)!)
    imageView.contentMode = .ScaleToFill
    cell.addSubview(imageView)
    
    //rasterize cell (for better performance)
    cell.layer.shouldRasterize = true
    cell.layer.rasterizationScale = UIScreen.mainScreen().scale
    
    return cell
  }
  
  // MARK: - UICollectionViewDelegate
  
  func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
    
    let MWbrowser: MWPhotoBrowser = MWPhotoBrowser(delegate: self)
    MWbrowser.setCurrentPhotoIndex(UInt(indexPath.row+1))
    self.navigationController!.pushViewController(MWbrowser, animated: true)
    
//    //load tapped photo
//    let url = NSURL(string: "http://192.168.0.19:8080/surfapp/api/photo/show/id/\(results[indexPath.row]["id"].intValue)")
//    let image = UIImage(data: NSData(contentsOfURL: url!)!)
//    
//    //push detail result view controller
//    let resultDetailViewController: SAResultDetailViewController = SAResultDetailViewController(image: image!)
//    self.navigationController!.pushViewController(resultDetailViewController, animated: true)
    
  }
  
  // MARK: - Previously taken photo recognizer
  
  func photoTapped(gestureRecognizer: UIGestureRecognizer) {
    
    let MWbrowser: MWPhotoBrowser = MWPhotoBrowser(delegate: self)
    MWbrowser.setCurrentPhotoIndex(0)
    self.navigationController!.pushViewController(MWbrowser, animated: true)
    
//    //load tapped photo
//    let imageView: UIImageView = gestureRecognizer.view as! UIImageView
//    
//    //push detail result view controller
//    let resultDetailViewController: SAResultDetailViewController = SAResultDetailViewController(image: imageView.image!)
//    self.navigationController!.pushViewController(resultDetailViewController, animated: true)
    
  }
  
  // MARK: - MWPhotoBrowserDelegate
  
  func numberOfPhotosInPhotoBrowser(photoBrowser: MWPhotoBrowser!) -> UInt {
    return UInt(MWphotos.count)
  }
  
  func photoBrowser(photoBrowser: MWPhotoBrowser!, photoAtIndex index: UInt) -> MWPhotoProtocol! {
    if Int(index) < MWphotos.count {
      return MWphotos[Int(index)] as! MWPhoto
    }
    return nil
  }
  
}
